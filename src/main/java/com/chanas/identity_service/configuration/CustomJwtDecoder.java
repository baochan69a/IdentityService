package com.chanas.identity_service.configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import com.chanas.identity_service.exception.AppException;
import com.chanas.identity_service.exception.ErrorCode;
import com.chanas.identity_service.repository.InvalidatedTokenRepository;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${spring.jwt.signerKey}")
    private String signerKey;

    // Inject thẳng Repository vào đây, không qua Service nữa
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        // 1. Khởi tạo NimbusJwtDecoder nếu chưa có
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            // --- BỔ SUNG ĐOẠN NÀY ĐỂ TẮT CLOCK SKEW ---
            // Set độ du di thời gian về 0 giây (hết hạn là chết luôn)
            JwtTimestampValidator jwtTimestampValidator = new JwtTimestampValidator(Duration.of(0, ChronoUnit.SECONDS));
            nimbusJwtDecoder.setJwtValidator(jwtTimestampValidator);
            // ------------------------------------------
        }

        try {
            // 2. Để Nimbus tự kiểm tra Chữ ký và Thời hạn trước
            Jwt jwt = nimbusJwtDecoder.decode(token);

            // 3. Lấy ra Token ID (JTI) để kiểm tra trong bảng Invalidated
            String tokenId = jwt.getId();

            // 4. Kiểm tra xem Token này đã bị Logout chưa
            if (invalidatedTokenRepository.existsById(tokenId)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // 5. Nếu mọi thứ OK, trả về jwt
            return jwt;

        } catch (Exception e) {
            // Bắt mọi lỗi (hết hạn, sai chữ ký, bị logout) và ném ra JwtException
            throw new JwtException("Token invalid");
        }
    }
}
