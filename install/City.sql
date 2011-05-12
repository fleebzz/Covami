-- phpMyAdmin SQL Dump
-- version 3.3.7deb5build0.10.10.1
-- http://www.phpmyadmin.net
--
-- Serveur: localhost
-- Généré le : Jeu 12 Mai 2011 à 10:42
-- Version du serveur: 5.1.49
-- Version de PHP: 5.3.3-1ubuntu9.5

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `covami_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `Ville`
--

CREATE TABLE IF NOT EXISTS `City` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `insee` varchar(255) DEFAULT NULL,
  `postalCode` varchar(255) DEFAULT NULL,
  `latitude` float NOT NULL,
  `longitude` float NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4ED433210A80272` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=79 ;

--
-- Contenu de la table `Ville`
--

INSERT INTO `City` (`id`, `insee`, `postalCode`, `latitude`, `longitude`, `name`, `country_id`) VALUES
(1, '75100', '75000', 48.8667, 2.33333, 'Paris', NULL),
(2, '51001', '51240', 49.8451, 2.82074, 'Ablaincourt', NULL),
(3, '62593', '62450', 50.0333, 2.86667, 'Morval', NULL),
(4, '62041', '62000', 50.2833, 2.78333, 'Arras', NULL),
(5, '62274', '62119', 50.4333, 2.98333, 'Dourges', NULL),
(6, '59350', '59800', 50.6333, 3.06667, 'Lille', NULL),
(7, '78499', '78730', 48.55, 1.9, 'Ponthévrard', NULL),
(8, '45093', '45520', 48.0333, 1.88333, 'Chevilly', NULL),
(9, '45234', '45100', 47.9167, 1.9, 'Orléans', NULL),
(10, '37261', '37100', 47.3833, 0.683333, 'Tours', NULL),
(11, '37154', '37250', 47.2833, 0.716667, 'Montbazon', NULL),
(12, '86194', '86000', 46.5833, 0.333333, 'Poitiers', NULL),
(13, '17415', '17100', 45.75, -0.633333, 'Saintes', NULL),
(14, '33063', '33300', 44.8333, -0.566667, 'Bordeaux', NULL),
(15, '72181', '72100', 48, 0.2, 'Le-Mans', NULL),
(16, '49007', '49000', 47.4667, -0.55, 'Angers', NULL),
(17, '44109', '44200', 47.2167, -1.55, 'Nantes', NULL),
(18, '76540', '76100', 49.4333, 1.08333, 'Rouen', NULL),
(19, '14528', '14130', 49.3333, 0.3, 'Quetteville', NULL),
(20, '14118', '14000', 49.1833, -0.35, 'Caen', NULL),
(21, '80021', '80090', 49.9, 2.3, 'Amiens', NULL),
(22, '62193', '62100', 50.95, 1.83333, 'Calais', NULL),
(23, '59183', '59240', 51.05, 2.36667, 'Dunkerque', NULL),
(24, '45104', '45120', 48.05, 2.7, 'Corquilleroy', NULL),
(25, '45115', '45320', 48.0333, 3.05, 'Courtenay', NULL),
(26, '89387', '89100', 48.1981, 3.28389, 'Sens', NULL),
(27, '82121', '82000', 44.0167, 1.35, 'Montauban', NULL),
(28, '19031', '19100', 45.15, 1.53333, 'Brive-La-Gaillarde', NULL),
(29, '87085', '87280', 45.85, 1.25, 'Limoges', NULL),
(30, '18279', '18100', 47.2167, 2.08333, 'Vierzon', NULL),
(31, '59122', '59400', 50.1667, 3.23333, 'Cambrai', NULL),
(32, '59606', '59300', 50.35, 3.53333, 'Valenciennes', NULL),
(33, '51454', '51100', 49.25, 4.03333, 'Reims', NULL),
(34, '51108', '51000', 48.9583, 4.36667, 'Châlons-en-Champagne', NULL),
(35, '67482', '67000', 48.5821, 7.44536, 'Strasbourg', NULL),
(36, '10387', '10000', 48.3, 4.08333, 'Troyes', NULL),
(37, '52269', '52200', 47.8667, 5.33333, 'Langres', NULL),
(38, '21231', '21000', 47.3167, 5.01667, 'Dijon', NULL),
(39, '69380', '69000', 45.75, 4.85, 'Lyon', NULL),
(40, '82186', '82400', 44.1901, 4.48242, 'Valence', NULL),
(41, '13001', '13100', 43.5333, 5.43333, 'Aix-en-Provence', NULL),
(42, '13200', '13000', 43.3, 5.4, 'Marseille', NULL),
(43, '83031', '83340', 43.4, 6.35, 'Le-Cannet-des-Maures', NULL),
(44, '6029', '6400', 43.55, 7.01667, 'Cannes', NULL),
(45, '66136', '66100', 42.6833, 2.88333, 'Perpignan', NULL),
(46, '11262', '11100', 43.1833, 3, 'Narbonne', NULL),
(47, '34172', '34080', 43.6, 3.88333, 'Montpellier', NULL),
(48, '30189', '30000', 43.8333, 4.35, 'Nimes', NULL),
(49, '31555', '31500', 43.6, 1.43333, 'Toulouse', NULL),
(50, '84087', '84100', 44.1333, 4.8, 'Orange', NULL),
(51, '2691', '2100', 49.85, 3.28333, 'Saint-Quentin', NULL),
(52, '76351', '76620', 49.5, 0.133333, 'Le-Havre', NULL),
(53, '54395', '54100', 48.6833, 6.2, 'Nancy', NULL),
(54, '57463', '57050', 49.1333, 6.16667, 'Metz', NULL),
(55, '68224', '68100', 47.75, 7.33333, 'Mulhouse', NULL),
(56, '1053', '1000', 46.2, 5.21667, 'Bourg-en-Bresse', NULL),
(57, '71270', '71870', 46.3, 4.83333, 'Macon', NULL),
(58, '1304', '1160', 46.05, 5.33333, 'Pont-d''Ain', NULL),
(59, '74012', '74100', 46.2, 6.25, 'Annemasse', NULL),
(60, '74056', '74400', 45.9167, 6.86667, 'Chamonix-Mont-Blanc', NULL),
(61, '74010', '74000', 45.9, 6.11667, 'Annecy', NULL),
(62, '73065', '73000', 45.5667, 5.93333, 'Chambéry', NULL),
(63, '38185', '38000', 45.1667, 5.71667, 'Grenoble', NULL),
(64, '83137', '83200', 43.1167, 5.93333, 'Toulon', NULL),
(65, '64102', '64100', 43.4833, -1.48333, 'Bayonne', NULL),
(66, '64483', '64500', 43.3833, -1.66667, 'Saint-Jean-de-Luz', NULL),
(67, '65440', '65000', 43.2333, 0.083333, 'Tarbes', NULL),
(68, '81099', '81600', 43.9, 1.91667, 'Gaillac', NULL),
(69, '18033', '18000', 47.0833, 2.4, 'Bourges', NULL),
(70, '63113', '63000', 45.7833, 3.08333, 'Clermont-Ferrand', NULL),
(71, '5036', '5380', 46.7598, 1.73828, 'Châteauroux', NULL),
(72, '58194', '58000', 46.9833, 3.16667, 'Nevers', NULL),
(73, '35238', '35000', 48.0833, -1.68333, 'Rennes', NULL),
(74, '53130', '53000', 48.0667, -0.766667, 'Laval', NULL),
(75, '76904', '76530', 46.77, -1.22498, 'Les-Essarts', NULL),
(76, '79191', '79000', 46.3167, -0.466667, 'Niort', NULL),
(77, '85191', '85000', 46.6667, -1.43333, 'La-Roche-sur-Yon', NULL),
(78, '17299', '17300', 45.9333, -0.983333, 'Rochefort', NULL);

--
-- Contraintes pour les tables exportées
--