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
  `app_description` text,
  `app_category` int(10) unsigned NOT NULL,
  `app_view` int(10) unsigned NOT NULL DEFAULT '0',
  `app_download` int(10) unsigned NOT NULL DEFAULT '0',
  `app_current_version` varchar(50) NOT NULL DEFAULT '1.0.0',
  `app_size` bigint(20) unsigned NOT NULL DEFAULT '0',
  `app_path` text,
  `app_screen_shoot` text,
  `app_icon` text,
  `published` bit(1) NOT NULL DEFAULT b'0',
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT NULL,
  `creater` bigint(20) unsigned NOT NULL,
  `updater` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`app_id`),
  KEY `app_name` (`app_name`,`app_category`,`app_view`,`app_download`,`app_size`,`created`,`updated`,`creater`,`updater`),
  KEY `published` (`published`),
  KEY `deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `app_category`
--

DROP TABLE IF EXISTS `app_category`;
CREATE TABLE IF NOT EXISTS `app_category` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` int(10) unsigned NOT NULL,
  `category_name` varchar(250) NOT NULL,
  `app_id` text NOT NULL,
  `type` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `type` (`type`),
  KEY `category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_download`
--

DROP TABLE IF EXISTS `app_download`;
CREATE TABLE IF NOT EXISTS `app_download` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) unsigned NOT NULL,
  `app_path` varchar(250) NOT NULL,
  `app_download_path` varchar(250) NOT NULL,
  `session_id` varchar(50) NOT NULL,
  `status` bit(1) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creater` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`),
  KEY `session_id` (`session_id`),
  KEY `status` (`status`),
  KEY `creater` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `app_version`
--

DROP TABLE IF EXISTS `app_version`;
CREATE TABLE IF NOT EXISTS `app_version` (
  `id` int(11) NOT NULL,
  `app_id` bigint(20) unsigned NOT NULL,
  `app_name` varchar(250) NOT NULL,
  `app_path` text,
  `app_icon` text,
  `app_screen_shoot` text,
  `app_size` bigint(20) unsigned NOT NULL DEFAULT '0',
  `app_version` varchar(50) NOT NULL DEFAULT '1.0.0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creater` bigint(20) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `app_view`
--

DROP TABLE IF EXISTS `app_view`;
CREATE TABLE IF NOT EXISTS `app_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) unsigned NOT NULL,
  `session_id` varchar(250) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `app_id` (`app_id`,`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `name` varchar(20) NOT NULL,
  `url` varchar(20) NOT NULL DEFAULT '',
  `parent` varchar(20) NOT NULL DEFAULT '0',
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `sort` int(2) unsigned NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`url`),
  UNIQUE KEY `name` (`name`)
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
  `ip` varchar(16),
  `user_agent` varchar(300),
  `link` text,
  `reference` text,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sessionId` (`session_id`,`account_id`,`device`,`action`,`ip`),
  KEY `created` (`created`),
  KEY `user_agent` (`user_agent`)
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
