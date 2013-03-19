-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Mar 18, 2013 at 09:50 AM
-- Server version: 5.5.27
-- PHP Version: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `kb_sicbok`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_INSERT`(IN `inUsername` VARCHAR(50) CHARSET utf8, IN `inPassword` VARCHAR(255) CHARSET utf8, IN `inEmail` VARCHAR(50) CHARSET utf8, IN `inFullname` VARCHAR(255) CHARSET utf8, IN `inDateCreate` VARCHAR(50) CHARSET utf8, IN `inBalance` VARCHAR(255) CHARSET utf8, IN `inBitcoinId` VARCHAR(255) CHARSET utf8, IN `inRegisterConfirmCode` VARCHAR(255) CHARSET utf8, IN `inIsActive` TINYINT(1))
    NO SQL
BEGIN
	#Routine body goes here...
	INSERT INTO 
	kb_user(`username`,
		`password`,
		`email`,
                `fullname`,
                `date_create`,
                `balance`,
                `bitcoin_id`,
                `register_confirm_code`,
                `is_active`)
	VALUES(
		inUsername,
                MD5(inPassword),
                inEmail,
                inFullname,
                inDateCreate,
                inBalance,
                inBitcoinId,
                inRegisterConfirmCode,
                inIsActive);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_CHECK_EXISTS`(IN `username_to_check` VARCHAR(50) CHARSET utf8, IN `email_to_check` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `username` = username_to_check
        OR `email` = email_to_check;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `kb_bet_history`
--

CREATE TABLE IF NOT EXISTS `kb_bet_history` (
  `bet_history_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `date_of_bet` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `is_win` tinyint(1) NOT NULL DEFAULT '0',
  `balance` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`bet_history_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `kb_bet_history_detail`
--

CREATE TABLE IF NOT EXISTS `kb_bet_history_detail` (
  `bet_history_detail_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `bet_history_id` int(11) NOT NULL,
  `bet_spot_id` int(11) NOT NULL,
  `is_win` tinyint(1) NOT NULL DEFAULT '0',
  `amount` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `balance` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`bet_history_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `kb_bet_pattern`
--

CREATE TABLE IF NOT EXISTS `kb_bet_pattern` (
  `bet_pattern_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `probability` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `odds` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `house_edge` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rtp` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`bet_pattern_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=16 ;

--
-- Dumping data for table `kb_bet_pattern`
--

INSERT INTO `kb_bet_pattern` (`bet_pattern_id`, `name`, `probability`, `odds`, `house_edge`, `rtp`) VALUES
(1, 'Big', '48.60', '1', '2.80', '97.20'),
(2, 'Small', '48.60', '1', '2.80', '97.20'),
(3, 'Specific ''Triples'' or ''Alls''', '0.46', '150', '30.10', '69.90'),
(4, 'Specific Doubles', '7.41', '8', '33.30', '66.70'),
(5, 'Any Triple or All ''Alls''', '2.80', '24', '30.60', '69.40'),
(6, 'Three Dice Total 4 or 17', '1.40', '50', '29.20', '70.80'),
(7, 'Three Dice Total 6 or 15', '4.60', '14', '30.60', '69.40'),
(8, 'Three Dice Total 5 or 16', '2.80', '18', '47.20', '52.80'),
(9, 'Three Dice Total 7 or 14', '6.90', '12', '9.70', '90.30'),
(10, 'Three Dice Total 8 or 13', '9.70', '8', '12.50', '87.50'),
(11, 'Three Dice Total 9 or 12', '11.60', '6', '19', '81.00'),
(12, 'Single Dice Bet 1', '34.72', '1', '7.90', '92.10'),
(13, 'Single Dice Bet 2', '6.94', '2', '7.90', '92.10'),
(14, 'Single Dice Bet 3', '0.46', '3', '7.90', '92.10'),
(15, 'Three Dice total 10 or 11', '12.50', '6', '12.50', '87.50');

-- --------------------------------------------------------

--
-- Table structure for table `kb_bet_spot`
--

CREATE TABLE IF NOT EXISTS `kb_bet_spot` (
  `bet_spot_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `bet_pattern_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`bet_spot_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=36 ;

--
-- Dumping data for table `kb_bet_spot`
--

INSERT INTO `kb_bet_spot` (`bet_spot_id`, `bet_pattern_id`, `name`) VALUES
(1, 1, 'Big'),
(2, 2, 'Small'),
(3, 3, 'Triple 1'),
(4, 3, 'Triple 2'),
(5, 3, 'Triple 3'),
(6, 3, 'Triple 4'),
(7, 3, 'Triple 5'),
(8, 3, 'Triple 6'),
(9, 4, 'Double 1'),
(10, 4, 'Double 2'),
(11, 4, 'Double 3'),
(12, 4, 'Double 4'),
(13, 4, 'Double 5'),
(14, 4, 'Double 6'),
(15, 5, 'All Triple'),
(16, 6, 'Three Dice = 4'),
(17, 6, 'Three Dice = 17'),
(18, 7, 'Three Dice Total = 6'),
(19, 7, 'Three Dice Total = 15'),
(20, 8, 'Three Dice Total = 5'),
(21, 8, 'Three Dice Total 16'),
(22, 9, 'Three Dice Total 7'),
(23, 9, 'Three Dice Total 14'),
(24, 10, 'Three Dice Total 8'),
(25, 10, 'Three Dice Total 13'),
(26, 11, 'Three Dice Total 9'),
(27, 11, 'Three Dice Total 12'),
(28, 12, 'Single Dice Bet 1'),
(29, 12, 'Single Dice Bet 2'),
(30, 12, 'Single Dice Bet 3'),
(31, 12, 'Single Dice Bet 4'),
(32, 12, 'Single Dice Bet 5'),
(33, 12, 'Single Dice Bet 6'),
(34, 15, 'Three Dice total = 10'),
(35, 15, 'Three Dice total = 11');

-- --------------------------------------------------------

--
-- Table structure for table `kb_user`
--

CREATE TABLE IF NOT EXISTS `kb_user` (
  `user_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(50) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `date_create` varchar(50) DEFAULT NULL,
  `balance` varchar(255) NOT NULL DEFAULT '1000',
  `bitcoin_id` varchar(255) DEFAULT NULL,
  `register_confirm_code` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `kb_user`
--

INSERT INTO `kb_user` (`user_id`, `username`, `password`, `email`, `fullname`, `date_create`, `balance`, `bitcoin_id`, `register_confirm_code`, `is_active`) VALUES
(1, 'vuongngocnam', '4e7e9270dc88cbd961ff5e12bad98fab', 'vuongngocnam@gmail.com', 'Kent Vuong', '1357872252', '1000', NULL, NULL, 0),
(2, 'dantenguyen', 'fcfa2501effa118ca1de03f2f455ef21', 'dantenguyen@gmail.com', 'Dante Nguyen', '1357872211', '1000', NULL, NULL, 0),
(3, 'namvuonghcm', '79bb2b42d2df57dcf57bc2a40df8dac8', 'namvuonghcm@gmail.com', 'Nam Vuong', '1357872223', '10', '', 'sdksdfjk', 0);

-- --------------------------------------------------------

--
-- Table structure for table `kb_user_history`
--

CREATE TABLE IF NOT EXISTS `kb_user_history` (
  `user_history_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `date_signin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_signout` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `reason_signout` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`user_history_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `kb_user_payment`
--

CREATE TABLE IF NOT EXISTS `kb_user_payment` (
  `user_payment_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type_of_payment` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1 [Deposit] 2[withdraw]',
  `date_of_payment` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `balance_before` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `balance_after` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
