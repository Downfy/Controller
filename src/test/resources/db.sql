--
-- Database: `mangtuyendungdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `authorities`
--

DROP TABLE IF EXISTS `authorities`;
CREATE TABLE IF NOT EXISTS `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `unique_uk_5` (`username`,`authority`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `email_campains`
--

DROP TABLE IF EXISTS `email_campains`;
CREATE TABLE IF NOT EXISTS `email_campains` (
  `id` bigint(20) NOT NULL,
  `start_campain` timestamp NULL DEFAULT NULL,
  `end_campain` timestamp NULL DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(200) NOT NULL,
  `template_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `email_templates`
--

DROP TABLE IF EXISTS `email_templates`;
CREATE TABLE IF NOT EXISTS `email_templates` (
  `id` varchar(50) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `body` longtext NOT NULL,
  `description` text,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `job_account`
--

DROP TABLE IF EXISTS `job_account`;
CREATE TABLE IF NOT EXISTS `job_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `preference_id` bigint(20) NOT NULL,
  `domain` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `preference_mobile` varchar(200) DEFAULT NULL,
  `mobile` varchar(100) DEFAULT NULL,
  `is_created` bit(1) NOT NULL DEFAULT b'0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=60792 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_apply`
--

DROP TABLE IF EXISTS `job_apply`;
CREATE TABLE IF NOT EXISTS `job_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT '',
  `job_id` varchar(50) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text NOT NULL,
  `is_viewed` bit(1) NOT NULL DEFAULT b'0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=58 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_company`
--

DROP TABLE IF EXISTS `job_company`;
CREATE TABLE IF NOT EXISTS `job_company` (
  `id` bigint(20) NOT NULL,
  `company_url` varchar(500) DEFAULT NULL,
  `company_name` varchar(500) NOT NULL,
  `company_name_english` varchar(500) DEFAULT NULL,
  `company_address` varchar(500) DEFAULT NULL,
  `company_phone` varchar(100) DEFAULT NULL,
  `company_tax_code` varchar(20) NOT NULL,
  `company_business_scope` varchar(200) DEFAULT NULL,
  `company_overview` text,
  `company_range` varchar(200) DEFAULT NULL,
  `company_website` varchar(100) DEFAULT NULL,
  `company_email` varchar(100) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `company_tax_code` (`company_tax_code`),
  KEY `company_email` (`company_email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `job_crawl`
--

DROP TABLE IF EXISTS `job_crawl`;
CREATE TABLE IF NOT EXISTS `job_crawl` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `title` varchar(250) NOT NULL,
  `url` varchar(300) DEFAULT '',
  `domain` varchar(50) DEFAULT 'mangtuyendung.vn',
  `boost` float NOT NULL DEFAULT '0',
  `company_name` varchar(200) NOT NULL,
  `company_overview` text,
  `company_address` varchar(300) DEFAULT NULL,
  `company_range` varchar(50) DEFAULT NULL,
  `job_category` varchar(500) NOT NULL,
  `job_location` varchar(200) NOT NULL,
  `job_time_work` varchar(100) DEFAULT NULL,
  `job_member_level` varchar(100) DEFAULT NULL,
  `job_salary` varchar(100) DEFAULT NULL,
  `job_age` varchar(100) DEFAULT NULL,
  `job_sex` varchar(50) DEFAULT NULL,
  `job_overview` text NOT NULL,
  `job_education_level` varchar(200) DEFAULT NULL,
  `job_experience_level` varchar(200) DEFAULT NULL,
  `job_requirement` text NOT NULL,
  `job_language` varchar(200) DEFAULT NULL,
  `job_contact_detail` varchar(500) DEFAULT NULL,
  `job_contact_name` varchar(100) DEFAULT NULL,
  `job_contact_address` varchar(300) DEFAULT NULL,
  `job_contact_person` varchar(300) DEFAULT NULL,
  `job_contact_phone` varchar(100) DEFAULT NULL,
  `job_contact_email` varchar(200) DEFAULT NULL,
  `job_expired` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1352693232446 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_education`
--

DROP TABLE IF EXISTS `job_education`;
CREATE TABLE IF NOT EXISTS `job_education` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profile_id` bigint(20) NOT NULL DEFAULT '0',
  `school_name` varchar(300) DEFAULT NULL,
  `school_field_of_study` varchar(300) DEFAULT NULL,
  `start_year` int(4) DEFAULT NULL,
  `end_year` int(4) DEFAULT NULL,
  `description` text,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=76812 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_location`
--

DROP TABLE IF EXISTS `job_location`;
CREATE TABLE IF NOT EXISTS `job_location` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL DEFAULT '',
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `sort` int(10) NOT NULL DEFAULT '0',
  `hot` bit(1) NOT NULL DEFAULT b'0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `url` (`url`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=74 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_member`
--

DROP TABLE IF EXISTS `job_member`;
CREATE TABLE IF NOT EXISTS `job_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(500) NOT NULL,
  `domain` varchar(50) NOT NULL,
  `boost` float NOT NULL,
  `person_name` varchar(200) DEFAULT NULL,
  `person_birthday` varchar(200) DEFAULT NULL,
  `person_address` varchar(200) DEFAULT NULL,
  `person_married` varchar(200) DEFAULT NULL,
  `person_sex` varchar(100) DEFAULT NULL,
  `person_mobile` varchar(200) DEFAULT NULL,
  `person_email` varchar(200) DEFAULT NULL,
  `job_title` varchar(300) DEFAULT NULL,
  `job_location` varchar(300) DEFAULT NULL,
  `job_category` varchar(300) DEFAULT NULL,
  `job_member_level` varchar(300) DEFAULT NULL,
  `job_work_form` varchar(300) DEFAULT NULL,
  `job_salary_current` varchar(300) DEFAULT NULL,
  `job_salary_desired` varchar(300) DEFAULT NULL,
  `job_skills` text,
  `job_desired` text,
  `job_sex` varchar(300) DEFAULT NULL,
  `job_education` text,
  `job_experience` text,
  `skill_education` varchar(200) DEFAULT NULL,
  `skill_it` varchar(200) DEFAULT NULL,
  `skill_comunication` varchar(200) DEFAULT NULL,
  `skill_english` varchar(200) DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `domain` (`domain`),
  KEY `person_mobile` (`person_mobile`),
  KEY `person_email` (`person_email`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1352612115021 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_position`
--

DROP TABLE IF EXISTS `job_position`;
CREATE TABLE IF NOT EXISTS `job_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profile_id` bigint(20) NOT NULL,
  `company_name` varchar(300) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `current_here` bit(1) NOT NULL DEFAULT b'0',
  `start_date_month` int(2) DEFAULT NULL,
  `end_date_month` int(2) DEFAULT NULL,
  `start_date_year` int(4) DEFAULT NULL,
  `end_date_year` int(4) DEFAULT NULL,
  `description` text,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=73270 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_search`
--

DROP TABLE IF EXISTS `job_search`;
CREATE TABLE IF NOT EXISTS `job_search` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(500) NOT NULL,
  `location` varchar(100) NOT NULL,
  `user_agent` text,
  `session_id` varchar(200) DEFAULT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `url` text,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `location` (`location`),
  KEY `ip` (`ip`),
  KEY `session_id` (`session_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=45729 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_skill`
--

DROP TABLE IF EXISTS `job_skill`;
CREATE TABLE IF NOT EXISTS `job_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profile_id` bigint(20) NOT NULL,
  `skill_name` varchar(200) NOT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=38 ;

-- --------------------------------------------------------

--
-- Table structure for table `map`
--

DROP TABLE IF EXISTS `map`;
CREATE TABLE IF NOT EXISTS `map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cat` varchar(100) NOT NULL,
  `map` varchar(200) NOT NULL DEFAULT '',
  `domain` varchar(50) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1600 ;

-- --------------------------------------------------------

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
CREATE TABLE IF NOT EXISTS `profiles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `person_name` varchar(200) DEFAULT NULL,
  `person_birthday` date DEFAULT NULL,
  `person_sex` varchar(20) DEFAULT NULL,
  `person_mobile` varchar(12) DEFAULT NULL,
  `person_married` varchar(50) DEFAULT NULL,
  `person_address` varchar(200) DEFAULT NULL,
  `current_title` varchar(200) DEFAULT NULL,
  `current_company` varchar(300) DEFAULT NULL,
  `level` int(11) NOT NULL DEFAULT '0',
  `resume_overview` text,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=54 ;

-- --------------------------------------------------------

--
-- Table structure for table `tracking_log`
--

DROP TABLE IF EXISTS `tracking_log`;
CREATE TABLE IF NOT EXISTS `tracking_log` (
  `id` bigint(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `campain_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------
--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(100) NOT NULL DEFAULT '',
  `parent` int(11) NOT NULL DEFAULT '0',
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `sort` int(11) NOT NULL DEFAULT '0',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `url` (`url`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0;

-- --------------------------------------------------------