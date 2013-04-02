-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 02, 2013 at 05:04 AM
-- Server version: 5.5.24-log
-- PHP Version: 5.4.3

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
CREATE DEFINER=`root`@`localhost` PROCEDURE `ADMIN_SELECT_ALL_USER`()
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_user;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ADMIN_SELECT_LOGIN`(IN `v_username` VARCHAR(255) CHARSET utf8, IN `v_password` VARCHAR(255) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_admin WHERE kb_admin.admin_name = v_username AND kb_admin.admin_password = MD5(v_password);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ADMIN_UPDATE_DELETE_USER`(IN `userId` INT(11), IN `delete_value` BIT)
    NO SQL
BEGIN
	#Routine body goes here...
	UPDATE kb_user SET kb_user.delete_flag = delete_value WHERE kb_user.user_id = userId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_HISTORY_DETAIL_INSERT`(IN `betHistoryId` INT(11), IN `betSpotId` INT(11), IN `isWin` TINYINT(1), IN `amount` VARCHAR(50) CHARSET utf8, IN `balance` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	INSERT INTO 
	kb_bet_history_detail(`bet_history_id`,
		`bet_spot_id`,
		`is_win`,
                `amount`,
                `balance`)
	VALUES( betHistoryId,
		betSpotId, 
		isWin,
                amount,
                balance);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_HISTORY_DETAIL_SELECT`(IN `betHistoryId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM `kb_bet_history_detail`
        WHERE `bet_history_id` = betHistoryId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_HISTORY_INSERT`(IN `userId` INT(11), IN `betDate` VARCHAR(50) CHARSET utf8, IN `isWin` TINYINT(1), IN `balance` VARCHAR(50) CHARSET utf8, IN `dice` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	INSERT INTO 
	kb_bet_history(`user_id`,
		`date_of_bet`,
		`is_win`,
                `balance`,
                `dices`)
	VALUES( userId,
		betDate, 
		isWin,
                balance,
                dice);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_HISTORY_SELECT_BY_USER_ID`(IN `userId` INT(11), IN `lastDate` DOUBLE, IN `mlimit` INT)
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM `kb_bet_history`
        WHERE `user_id` = userId AND (kb_bet_history.date_of_bet < lastDate OR lastDate is NULL) ORDER BY kb_bet_history.date_of_bet DESC LIMIT mlimit;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_PATTERN_SELECT_BY_ID`(IN `patternId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_bet_pattern
        WHERE `bet_pattern_id` = patternId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BET_SPOT_SELECT_BY_ID`(IN `spotId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_bet_spot
        WHERE `bet_spot_id` = spotId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_CHECK_FB_EMAIL_EXIST`(IN `user_email` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
  DECLARE result VARCHAR(50);
	DECLARE result2 VARCHAR(50);
	SELECT email INTO result FROM kb_user WHERE `email` = user_email;
	#Facebook email is not exist => Register
	IF result IS NULL THEN SELECT 1 AS 'result';
	#Email exist
	ELSE
		#Check this email is facebook account
		SELECT email INTO result2 FROM kb_user 
		WHERE `email` = user_email 
		AND `is_facebook_connected` = 1;
		#This email is not a facebook account ->Invalid
		IF result2 IS NULL THEN SELECT 2  AS 'result';
		#This email is exist => valid => Login
		ELSE SELECT 3 AS 'result';
		END IF;
	END IF;
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_PAYMENT_INSERT`(IN `userId` INT(11), IN `typeOfPayment` TINYINT(1), IN `dateOfPayment` VARCHAR(50) CHARSET utf8, IN `balanceBefore` VARCHAR(255) CHARSET utf8, IN `balanceAfter` VARCHAR(255) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	INSERT INTO 
	kb_user_payment(`user_id`,
		`type_of_payment`,
		`date_of_payment`,
                `balance_before`,
                `balance_after`)
	VALUES( userId,
		typeOfPayment, 
		dateOfPayment,
                balanceBefore,
                balanceAfter);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_CHECK_EXISTS`(IN `username_to_check` VARCHAR(50) CHARSET utf8, IN `email_to_check` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `username` = username_to_check
        OR `email` = email_to_check;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_FACEBOOK_LOGIN`(IN `user_email` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_user WHERE `email` = user_email;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_FORGOT_PASSWORD`(IN `email_to_check` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `email` = email_to_check;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_FORGOT_PASS_CONFIRM_CODE`(IN `email_to_check` VARCHAR(50) CHARSET utf8, IN `code_to_check` VARCHAR(255) CHARSET utf8)
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `email` = email_to_check
        AND `forgot_pass_confirm_code` = code_to_check;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_IS_VALID_PASS`(IN `old_pass_to_check` VARCHAR(255) CHARSET utf8, IN `userId` INT(11))
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `password` = MD5(old_pass_to_check)
        AND `user_id` = userId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_SIGNUP_CONFIRM`(IN `email_to_check` VARCHAR(50) CHARSET utf8, IN `code_to_check` VARCHAR(255) CHARSET utf8)
    NO SQL
BEGIN
	SELECT * FROM kb_user
        WHERE `email` = email_to_check
        AND `register_confirm_code` = code_to_check;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_SIGN_IN`(IN `username_to_check` VARCHAR(50) CHARSET utf8, IN `pass_to_check` VARCHAR(255) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_user
        WHERE `username` = username_to_check
        AND `password` = MD5(pass_to_check)
        AND `is_active` = 1
        AND `is_lock` = 0;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_SELECT_USER_BY_ID`(IN `uesrId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	SELECT * FROM kb_user
        WHERE `user_id` = uesrId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_UPDATE_ACTIVATION`(IN `email_to_confirm` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	UPDATE kb_user
        SET `is_active` = 1
        WHERE `email` = email_to_confirm;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_UPDATE_BALANCE`(IN `newBalance` VARCHAR(50) CHARSET utf8, IN `userId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	UPDATE kb_user 
        SET `balance` = newBalance
        WHERE `user_id` = userId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_UPDATE_BITCOIN_ID`(IN `newBitcoinId` VARCHAR(255) CHARSET utf8, IN `userId` INT(11))
    NO SQL
BEGIN
	#Routine body goes here...
	UPDATE kb_user 
        SET `bitcoin_id` = newBitcoinId
        WHERE `user_id` = userId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_UPDATE_FORGOT_PASS_CONFIRM_CODE`(IN `forgotPassConfirmCode` VARCHAR(255) CHARSET utf8, IN `e_mail` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	#Routine body goes here...
	UPDATE `kb_user`
        SET `forgot_pass_confirm_code` = forgotPassConfirmCode
        WHERE `email` = e_mail;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `USER_UPDATE_PASS_BY_EMAIL`(IN `new_password` VARCHAR(255) CHARSET utf8, IN `email_to_change` VARCHAR(50) CHARSET utf8)
    NO SQL
BEGIN
	UPDATE kb_user
        SET `password` = MD5(new_password)
        WHERE `email` = email_to_change;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `kb_admin`
--

CREATE TABLE IF NOT EXISTS `kb_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `admin_password` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2 ;

--
-- Dumping data for table `kb_admin`
--

INSERT INTO `kb_admin` (`id`, `admin_name`, `admin_password`) VALUES
(1, 'dante', 'e10adc3949ba59abbe56e057f20f883e');

-- --------------------------------------------------------

--
-- Table structure for table `kb_bet_history`
--

CREATE TABLE IF NOT EXISTS `kb_bet_history` (
  `bet_history_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `date_of_bet` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `dices` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_win` tinyint(1) NOT NULL DEFAULT '0',
  `balance` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`bet_history_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=150 ;

--
-- Dumping data for table `kb_bet_history`
--

INSERT INTO `kb_bet_history` (`bet_history_id`, `user_id`, `date_of_bet`, `dices`, `is_win`, `balance`) VALUES
(99, 3, '1363941133', '4|4|4', 1, '750.0'),
(100, 3, '1363941152', '3|3|3', 1, '800.0'),
(101, 3, '1363941171', '6|6|6', 0, '800.0'),
(102, 3, '1363941199', '4|4|4', 0, '800.0'),
(103, 3, '1363941240', '6|6|2', 0, '800.0'),
(104, 3, '1363941247', '2|1|1', 0, '800.0'),
(105, 9, '1363942816', '3|2|2', 0, '1624.0'),
(106, 9, '1363943021', '2|5|2', 1, '1649.0'),
(107, 9, '1363943243', '3|6|5', 0, '1644.0'),
(108, 9, '1363943366', '5|4|2', 0, '1614.0'),
(109, 9, '1363943719', '1|5|1', 0, '1589.0'),
(110, 9, '1363947493', '3|3|4', 0, '1585.0'),
(111, 9, '1364200343', '5|2|1', 0, '1555.0'),
(112, 3, '1364439255', '5|6|2', 1, '850.0'),
(113, 3, '1364439259', '1|2|2', 0, '800.0'),
(114, 3, '1364439263', '5|1|2', 0, '750.0'),
(115, 3, '1364445875', '2|4|6', 1, '800.0'),
(116, 3, '1364445885', '5|6|4', 1, '850.0'),
(117, 3, '1364445887', '1|4|1', 0, '800.0'),
(118, 3, '1364451695', '4|6|4', 1, '850.0'),
(119, 3, '1364460565', '6|2|6', 1, '900.0'),
(120, 3, '1364463052', '5|6|2', 1, '950.0'),
(121, 3, '1364463073', '3|3|6', 1, '1000.0'),
(122, 3, '1364528298', '4|1|4', 0, '950.0'),
(123, 3, '1364528318', '4|3|3', 0, '900.0'),
(124, 3, '1364528320', '2|2|5', 0, '850.0'),
(125, 3, '1364528320', '5|1|1', 0, '800.0'),
(126, 3, '1364528321', '4|6|3', 1, '850.0'),
(127, 3, '1364528322', '4|4|4', 0, '800.0'),
(128, 3, '1364528324', '5|4|6', 1, '850.0'),
(129, 3, '1364528324', '4|5|2', 1, '900.0'),
(130, 3, '1364528326', '5|5|4', 1, '950.0'),
(131, 3, '1364528327', '5|6|4', 1, '1000.0'),
(132, 3, '1364528329', '6|6|1', 1, '1050.0'),
(133, 3, '1364528329', '6|3|3', 1, '1100.0'),
(134, 3, '1364528331', '1|2|5', 0, '1050.0'),
(135, 3, '1364529486', '2|5|2', 0, '1000.0'),
(136, 3, '1364541334', '3|4|6', 1, '1050.0'),
(137, 3, '1364541334', '3|4|4', 1, '1100.0'),
(138, 3, '1364541338', '5|2|2', 0, '1050.0'),
(139, 3, '1364541340', '6|5|5', 1, '1100.0'),
(140, 3, '1364541343', '4|6|6', 1, '1150.0'),
(141, 3, '1364541344', '6|5|1', 1, '1200.0'),
(142, 3, '1364542995', '2|2|4', 0, '1150.0'),
(143, 3, '1364542998', '6|2|1', 0, '1100.0'),
(144, 3, '1364547896', '4|4|5', 1, '1050.0'),
(145, 3, '1364548002', '1|6|3', 0, '1000.0'),
(146, 3, '1364548008', '1|6|4', 1, '1050.0'),
(147, 3, '1364548026', '4|3|6', 1, '1100.0'),
(148, 3, '1364548503', '6|6|6', 0, '1050.92'),
(149, 3, '1364551531', '1|4|5', 0, '1021.3351000000007');

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=170 ;

--
-- Dumping data for table `kb_bet_history_detail`
--

INSERT INTO `kb_bet_history_detail` (`bet_history_detail_id`, `bet_history_id`, `bet_spot_id`, `is_win`, `amount`, `balance`) VALUES
(108, 99, 1, 1, '50.0', '750.0'),
(109, 100, 1, 1, '50.0', '800.0'),
(110, 101, 1, 1, '50.0', '800.0'),
(111, 101, 2, 0, '50.0', '800.0'),
(112, 102, 1, 0, '50.0', '800.0'),
(113, 102, 2, 1, '50.0', '800.0'),
(114, 103, 1, 1, '50.0', '800.0'),
(115, 103, 2, 0, '50.0', '800.0'),
(116, 104, 1, 0, '50.0', '800.0'),
(117, 104, 2, 1, '50.0', '800.0'),
(118, 105, 15, 0, '3.0', '1624.0'),
(119, 106, 26, 1, '5.0', '1649.0'),
(120, 106, 31, 0, '5.0', '1649.0'),
(121, 107, 15, 0, '5.0', '1644.0'),
(122, 108, 15, 0, '30.0', '1614.0'),
(123, 109, 31, 0, '5.0', '1589.0'),
(124, 109, 15, 0, '20.0', '1589.0'),
(125, 110, 1, 0, '1.0', '1585.0'),
(126, 110, 2, 1, '1.0', '1585.0'),
(127, 110, 15, 0, '4.0', '1585.0'),
(128, 111, 10, 0, '5.0', '1555.0'),
(129, 111, 12, 0, '5.0', '1555.0'),
(130, 111, 4, 0, '5.0', '1555.0'),
(131, 111, 15, 0, '15.0', '1555.0'),
(132, 112, 1, 1, '50.0', '850.0'),
(133, 113, 1, 0, '50.0', '800.0'),
(134, 114, 1, 0, '50.0', '750.0'),
(135, 115, 1, 1, '50.0', '800.0'),
(136, 116, 1, 1, '50.0', '850.0'),
(137, 117, 1, 0, '50.0', '800.0'),
(138, 118, 1, 1, '50.0', '850.0'),
(139, 119, 1, 1, '50.0', '900.0'),
(140, 120, 1, 1, '50.0', '950.0'),
(141, 121, 1, 1, '50.0', '1000.0'),
(142, 122, 1, 0, '50.0', '950.0'),
(143, 123, 1, 0, '50.0', '900.0'),
(144, 124, 1, 0, '50.0', '850.0'),
(145, 125, 1, 0, '50.0', '800.0'),
(146, 126, 1, 1, '50.0', '850.0'),
(147, 127, 1, 0, '50.0', '800.0'),
(148, 128, 1, 1, '50.0', '850.0'),
(149, 129, 1, 1, '50.0', '900.0'),
(150, 130, 1, 1, '50.0', '950.0'),
(151, 131, 1, 1, '50.0', '1000.0'),
(152, 132, 1, 1, '50.0', '1050.0'),
(153, 133, 1, 1, '50.0', '1100.0'),
(154, 134, 1, 0, '50.0', '1050.0'),
(155, 135, 1, 0, '50.0', '1000.0'),
(156, 136, 1, 1, '50.0', '1050.0'),
(157, 137, 1, 1, '50.0', '1100.0'),
(158, 138, 1, 0, '50.0', '1050.0'),
(159, 139, 1, 1, '50.0', '1100.0'),
(160, 140, 1, 1, '50.0', '1150.0'),
(161, 141, 1, 1, '50.0', '1200.0'),
(162, 142, 1, 0, '50.0', '1150.0'),
(163, 143, 1, 0, '50.0', '1100.0'),
(164, 144, 1, 1, '50.0', '1050.0'),
(165, 145, 1, 0, '50.0', '1000.0'),
(166, 146, 1, 1, '50.0', '1050.0'),
(167, 147, 1, 1, '50.0', '1100.0'),
(168, 148, 1, 0, '50.0', '1050.92'),
(169, 149, 1, 0, '50.0', '1021.3351000000007');

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
  `forgot_pass_confirm_code` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '0',
  `is_facebook_connected` tinyint(1) NOT NULL DEFAULT '0',
  `is_trial` tinyint(1) NOT NULL DEFAULT '1',
  `is_lock` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

--
-- Dumping data for table `kb_user`
--

INSERT INTO `kb_user` (`user_id`, `username`, `password`, `email`, `fullname`, `date_create`, `balance`, `bitcoin_id`, `register_confirm_code`, `forgot_pass_confirm_code`, `is_active`, `is_facebook_connected`, `is_trial`, `is_lock`) VALUES
(1, 'vuongngocnam', '4e7e9270dc88cbd961ff5e12bad98fab', 'vuongngocnam@gmail.com', 'Kent Vuong', '1357872252', '1000', NULL, NULL, 'ddddd', 0, 0, 1, 0),
(2, 'dantenguyen', 'fcfa2501effa118ca1de03f2f455ef21', 'dantenguyen@gmail.com', 'Dante Nguyen', '1357872211', '1000', NULL, NULL, 'ddddd', 0, 0, 1, 0),
(3, 'namvuonghcm', '79bb2b42d2df57dcf57bc2a40df8dac8', 'namvuonghcm@gmail.com', 'Nam Vuong', '1357872223', '1554.989', '1FVbEosFVUhXVoWW7RBkCa9i5i4q6ChCdS', 'sdksdfjk', '26666962417B045329809A2B74EA0E7F3A3DB07CD4A4323AEBBD2A3E51724C91', 1, 0, 1, 0),
(4, 'kentvuong', '363a530babcb58b44ac1807cb4adbc47', 'idvvn90@gmail.com', 'Kent Vuong', '1363597816', '1000.0', NULL, 'BDF22796C7225085F0531D7A67FBDCE5A6151FBF885DC7A7E39E0A2725F0DB8A', 'ddddd', 1, 0, 1, 0),
(9, 'borain', '92eb5ffee6ae2fec3ad71c777531578f', 'trungthuc89@gmail.com', 'x', '1363688709', '1555.0', NULL, 'A435E894D9CE6BE0FC90033EC62A129D58725A2DF017811F79E81655E6CB7B13', 'ddddd', 1, 0, 1, 0),
(10, 'dantruong', '0cc175b9c0f1b6a831c399e269772661', 'cotamthoi@gmail.com', 'thuc', '1363768265', '1000.0', NULL, '5B3ABB66E87DAF030FB5671751907059E8377F2A215FCF59770259891B83BE79', 'ddddd', 0, 0, 1, 0),
(11, 'dante', '499b90fe8425c8823733ae9b74346c88', 'an.nguyenquocduy@gmail.com', 'nguyen an', '1363768975', '1000.0', NULL, '475C9D6E047CFC4F753588CE2C749D5A88011C6E7FD9E94F24D364209375C0D1', 'ddddd', 1, 0, 1, 0),
(12, 'dgxv', 'dd4b1ddf9a2782926619cc7d5db618c0', 'hshdd@gmf.com', 'dkdjfn', '1363854273', '1000.0', NULL, '0D3C56DD45AE3D61FB32C8DD76C6BCB1953D6BA18DA6F8A75FA57D4006DD1E50', 'ddddd', 0, 0, 1, 0),
(13, 'dante123456', 'e10adc3949ba59abbe56e057f20f883e', 'a1provip002@gmail.com', 'dante', '1363856194', '985.0', NULL, '650E6CD59B2F0CDDAF6C6E0C8B08DDD2C629C2352BFDEBAC487452C841CB22C5', NULL, 1, 0, 1, 0),
(14, 'gg', '585790f98ff1dd61412aad36654e0371', 'vhj', 'vggh', '1363862216', '1000.0', NULL, '2DEE3AAC2B5564BEBD22608B0D6CA2E5DF5519D393045E95013177BCBA3DBF9C', NULL, 0, 0, 1, 0),
(15, 'g', '19b19ffc30caef1c9376cd2982992a59', 'gh', 'fg', '1363862272', '1000.0', NULL, '7B7B5D686B02751A6B078098DDFDCB81D3F9028E6DB14C317DCBD860393F91F1', NULL, 0, 0, 1, 0),
(16, 'gj', '19b19ffc30caef1c9376cd2982992a59', 'ft', 'gh', '1363862400', '1000.0', NULL, '60F6EA4D2DAF0A0B7EAE1CA78E7D0AE14C559688379BE7A56D5AAAEE4780197B', NULL, 0, 0, 1, 0);

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
  `user_id` int(11) NOT NULL,
  `type_of_payment` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1 [Deposit] 2[withdraw]',
  `date_of_payment` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `balance_before` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `balance_after` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_payment_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=4 ;

--
-- Dumping data for table `kb_user_payment`
--

INSERT INTO `kb_user_payment` (`user_payment_id`, `user_id`, `type_of_payment`, `date_of_payment`, `balance_before`, `balance_after`) VALUES
(2, 3, 1, '1364551578', '1021.3351000000007', '1022.2620000000007'),
(3, 3, 2, '1364813969', '1555.0', '1554.989');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
