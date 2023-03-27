
use devjeans;

CREATE TABLE `account` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `first_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `last_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `profile_picture_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           `roles` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `photo` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `thumbnail_image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `image_key_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `thumbnail_image_key_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `photo_title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `user_id` bigint NOT NULL,
                         `like_count` int NOT NULL DEFAULT '0',
                         `visit_count` int NOT NULL DEFAULT '0',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `created_date` datetime(6) DEFAULT NULL,
                         `last_modified_date` datetime(6) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_user_photo` (`user_id`),
                         CONSTRAINT `fk_user_photo` FOREIGN KEY (`user_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
