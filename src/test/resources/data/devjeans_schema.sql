use devjeans;

CREATE TABLE `account` (
                         id BIGINT(20) NOT NULL AUTO_INCREMENT,
                         first_name VARCHAR(255),
                         last_name VARCHAR(255),
                         email VARCHAR(255) NOT NULL,
                         profile_picture_url VARCHAR(255),
                         roles VARCHAR(255),
                         `is_blocked_user` tinyint(1) NOT NULL DEFAULT '0',
                         PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `photo` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `image_url` varchar(255) NOT NULL,
                         `thumbnail_image_url` varchar(255) NOT NULL,
                         `image_key_name` varchar(255) NOT NULL,
                         `thumbnail_image_key_name` varchar(255) NOT NULL,
                         `photo_title` varchar(255) NOT NULL,
                         `like_count` int(11) NOT NULL,
                         `visit_count` int(11) NOT NULL,
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `user_created_id` bigint(20) NOT NULL,
                         `created_date` datetime(6) DEFAULT NULL,
                         `last_modified_date` datetime(6) DEFAULT NULL,
                         `version` bigint NOT NULL DEFAULT 0,
                         `is_show` tinyint(1) NOT NULL DEFAULT '1',
                         `reported_count` int NOT NULL DEFAULT '0',
                         PRIMARY KEY (`id`),
                         KEY `FKsnv9fh0m56b0iwyxf2cyf2ovv` (`user_created_id`),
                         CONSTRAINT `FKsnv9fh0m56b0iwyxf2cyf2ovv` FOREIGN KEY (`user_created_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE account_liked_photos (
                                      account_id BIGINT NOT NULL,
                                      photo_id BIGINT NOT NULL,
                                      PRIMARY KEY (account_id, photo_id),
                                      CONSTRAINT FK_account_liked_photos_account FOREIGN KEY (account_id) REFERENCES account (id),
                                      CONSTRAINT FK_account_liked_photos_photo FOREIGN KEY (photo_id) REFERENCES photo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE report (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT,
                        photo_id BIGINT,
                        report_type VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (user_id) REFERENCES account (id),
                        FOREIGN KEY (photo_id) REFERENCES photo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
