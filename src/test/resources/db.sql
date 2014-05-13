-- phpMyAdmin SQL Dump
-- version 4.0.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 17, 2014 at 01:58 PM
-- Server version: 5.5.34-0ubuntu0.13.10.1
-- PHP Version: 5.5.3-1ubuntu2.1
DROP DATABASE mystoredb;
CREATE DATABASE mystoredb;
USE mystoredb;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `mangtuyendungdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `apps`
--

DROP TABLE IF EXISTS `apps`;
CREATE TABLE IF NOT EXISTS `apps` (
  `app_id` bigint(20) unsigned NOT NULL,
  `app_name` varchar(250) NOT NULL,
  `app_description` text DEFAULT NULL,
  `app_review_title` varchar(160) DEFAULT NULL,
  `app_review_description` text DEFAULT NULL,
  `app_author` varchar(100) DEFAULT NULL,
  `app_category` varchar(20) DEFAULT NULL,
  `app_view` int(10) unsigned NOT NULL DEFAULT '0',
  `app_download` int(10) unsigned NOT NULL DEFAULT '0',
  `app_package` varchar(50) DEFAULT NULL,
  `app_icon` varchar(200) DEFAULT NULL,
  `status` int(1) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT NULL,
  `creater` bigint(20) unsigned NOT NULL,
  `updater` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`app_id`),
  KEY `published` (`status`),
  KEY `app_name` (`app_name`),
  KEY `app_category` (`app_category`),
  KEY `app_view` (`app_view`),
  KEY `app_download` (`app_download`),
  KEY `app_package` (`app_package`),
  KEY `created` (`created`),
  KEY `updated` (`updated`),
  KEY `creater` (`creater`),
  KEY `updater` (`updater`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `app_category`
--

DROP TABLE IF EXISTS `app_category`;
CREATE TABLE IF NOT EXISTS `app_category` (
  `id` int(10) unsigned NOT NULL,
  `category_id` int(10) unsigned NOT NULL,
  `category_name` varchar(250) NOT NULL,
  `app_id` bigint(20) NOT NULL,
  `type` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `type` (`type`),
  KEY `category_name` (`category_name`),
  KEY `app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_download`
--

DROP TABLE IF EXISTS `app_download`;
CREATE TABLE IF NOT EXISTS `app_download` (
  `id` bigint(20) unsigned NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  `app_version` varchar(250) NOT NULL,
  `app_type` int(11) NOT NULL DEFAULT '0',
  `session_id` varchar(50) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creater` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  KEY `session_id` (`session_id`),
  KEY `creater` (`creater`),
  KEY `app_type` (`app_type`),
  KEY `created` (`created`),
  KEY `creater_2` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_screen_shoots`
--

DROP TABLE IF EXISTS `app_screen_shoots`;
CREATE TABLE IF NOT EXISTS `app_screen_shoots` (
  `id` bigint(20) unsigned NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  `app_screen_shoot` varchar(200) NOT NULL,
  `size` bigint(20) unsigned NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creater` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  KEY `status` (`status`),
  KEY `created` (`created`),
  KEY `creater` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_version`
--

DROP TABLE IF EXISTS `app_version`;
CREATE TABLE IF NOT EXISTS `app_version` (
  `id` bigint(20) unsigned NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  `app_path` varchar(200) DEFAULT NULL,
  `app_package` varchar(50) DEFAULT NULL,
  `app_version` varchar(10) DEFAULT NULL,
  `app_size` bigint(20) unsigned NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT NULL,
  `creater` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  KEY `app_version` (`app_version`),
  KEY `status` (`status`),
  KEY `created` (`created`),
  KEY `creater` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_view`
--

DROP TABLE IF EXISTS `app_view`;
CREATE TABLE IF NOT EXISTS `app_view` (
  `id` bigint(20) unsigned NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  `session_id` varchar(250) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`,`session_id`),
  KEY `created` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `name` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL DEFAULT '',
  `parent` varchar(20) DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `sort` int(2) unsigned NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `url` (`url`),
  KEY `parent` (`parent`),
  KEY `enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `developers`
--

DROP TABLE IF EXISTS `developers`;
CREATE TABLE IF NOT EXISTS `developers` (
  `id` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL DEFAULT '',
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE IF NOT EXISTS `logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id` varchar(100) NOT NULL,
  `account_id` bigint(20) NOT NULL DEFAULT '0',
  `device` int(11) unsigned NOT NULL,
  `action` int(11) unsigned NOT NULL,
  `ip` varchar(16) DEFAULT NULL,
  `user_agent` varchar(300) DEFAULT NULL,
  `link` text,
  `reference` text,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `created` (`created`),
  KEY `user_agent` (`user_agent`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL,
  `password` varchar(200) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `email` varchar(50) NOT NULL,
  `firstName` varchar(200) NOT NULL,
  `lastName` varchar(200) NOT NULL,
  `expiration_date` timestamp NULL DEFAULT NULL,
  `active_key` varchar(500) DEFAULT NULL,
  `failed_login_count` int(11) NOT NULL DEFAULT '0',
  `last_failed_login_time` timestamp NULL DEFAULT NULL,
  `last_host_address` varchar(50) DEFAULT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `last_password_change_time` timestamp NULL DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `enabled` (`enabled`),
  KEY `created` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
CREATE TABLE IF NOT EXISTS `news` (
  `id` bigint(20) NOT NULL,
  `app_id` bigint(20) NULL,
  `title` varchar(200) NOT NULL,
  `description` tinyint(1) NOT NULL DEFAULT '0',
  `icon` varchar(200) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '0',
  `type` int(1) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT NULL,
  `creater` bigint(20) unsigned NOT NULL,
  `updater` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  KEY `created` (`created`),
  KEY `updated` (`updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `new_compare`
--

DROP TABLE IF EXISTS `new_compare`;
CREATE TABLE IF NOT EXISTS `new_compare` (
  `news_id` bigint(20) NOT NULL,
  `app_id` varchar(200) NOT NULL,
  PRIMARY KEY (`news_id`, `app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `UserConnection`
--

DROP TABLE IF EXISTS `UserConnection`;
CREATE TABLE IF NOT EXISTS `UserConnection` (
    `userId` varchar(255) not null,
    `providerId` varchar(255) not null,
    `providerUserId` varchar(255),
    `rank` int not null,
    `displayName` varchar(255),
    `profileUrl` varchar(512),
    `imageUrl` varchar(512),
    `accessToken` varchar(255) not null,
    `secret` varchar(255),
    `refreshToken` varchar(255),
    `expireTime` bigint,
  PRIMARY KEY (`userId`, `providerId`, `providerUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create unique index UserConnectionRank on UserConnection(userId, providerId, rank);
