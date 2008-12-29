/*
SQLyog - Free MySQL GUI v5.19
Host - 5.0.45-community-nt-log : Database - testdb
*********************************************************************
Server version : 5.0.45-community-nt-log
*/

SET NAMES utf8;

SET SQL_MODE='';
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';

/*Data for the table `address` */

insert into `address` (`id`,`city`,`state`,`street`,`zip`) values (1,'Chicago','IL','940 N Fairfield','60610');
insert into `address` (`id`,`city`,`state`,`street`,`zip`) values (2,'Chicago','IL','734 N Fairfield, Apt 3','60610');
insert into `address` (`id`,`city`,`state`,`street`,`zip`) values (3,'Chicago','IL','3290 W Fulton','60610');

/*Data for the table `home` */

insert into `home` (`id`,`type`,`address_id`) values (1,'house',1);
insert into `home` (`id`,`type`,`address_id`) values (2,'apartment',2);
insert into `home` (`id`,`type`,`address_id`) values (3,'house',3);

/*Data for the table `ingredient` */

insert into `ingredient` (`id`,`name`) values (1,'Sugar');
insert into `ingredient` (`id`,`name`) values (2,'Butter');
insert into `ingredient` (`id`,`name`) values (3,'Flour');
insert into `ingredient` (`id`,`name`) values (4,'Salt');
insert into `ingredient` (`id`,`name`) values (5,'Yeast');
insert into `ingredient` (`id`,`name`) values (6,'Chicken');

/*Data for the table `person` */

insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (1,65,'1943-12-29 14:12:41','Grandpa','Alpha',100.65,NULL,3,NULL);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (2,65,'1943-12-29 14:12:41','Grandma','Alpha',100.65,NULL,3,NULL);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (3,39,'1969-12-29 14:12:41','Papa','Alpha',100.39,1,1,2);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (4,40,'1968-12-29 14:12:41','Mama','Alpha',100.4,NULL,1,NULL);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (5,39,'1969-12-29 14:12:41','Papa','Beta',100.39,NULL,2,NULL);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (6,38,'1970-12-29 14:12:41','Mama','Beta',100.38,1,2,2);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (7,10,'1998-12-29 14:12:41','Joe','Alpha',100.1,3,1,4);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (8,9,'1999-12-29 14:12:41','Sally','Alpha',100.09,3,1,4);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (9,10,'1998-12-29 14:12:41','Joe','Beta',100.1,5,2,6);
insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (10,14,'1994-12-29 14:12:41','Margret','Beta',100.14,5,2,6);

/*Data for the table `pet` */

insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (1,1,4444,'Jimmy',NULL,'spider','\0',1);
insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (0,2,1111,'Mr','Wiggles','fish',NULL,1);
insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (1,3,2222,'Miss','Prissy','cat','\0',2);
insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (1,4,3333,'Norman',NULL,'cat','\0',1);

/*Data for the table `pet_limbs` */

insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left front leg',0);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'right front leg',1);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left frontish leg',2);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'right frontish leg',3);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left hindish leg',4);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'right hindish leg',5);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left hind leg',6);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'right hind leg',7);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (3,'left front leg',0);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (3,'right front leg',1);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (3,'left hind leg',2);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (3,'right hind leg',3);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (4,'left front leg',0);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (4,'right front leg',1);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (4,'left hind leg',2);
insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (4,'right hind leg',3);

/*Data for the table `recipe` */

insert into `recipe` (`id`,`title`) values (1,'Bread');
insert into `recipe` (`id`,`title`) values (2,'Toffee');
insert into `recipe` (`id`,`title`) values (3,'Fried Chicken');

/*Data for the table `recipe_x_ingredient` */

insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (2,'cups',2,1);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (2,'cups',2,2);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (0.25,'cup',3,2);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (4,'cups',1,3);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (1,'cup',3,3);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (0.5,'tsp.',1,4);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (0.5,'tsp.',3,4);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (1,'Tbs.',1,5);
insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_id`) values (6,'pieces',3,6);

/*Data for the table `store` */

insert into `store` (`id`,`name`) values (1,'Tom\'s Convenience Store');
insert into `store` (`id`,`name`) values (2,'Billy\'s Mini-Mart');

/*Data for the table `store_ingredient` */

insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (1,1);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (1,2);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (1,4);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,1);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,2);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,3);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,4);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,5);
insert into `store_ingredient` (`Store_id`,`ingredientsCarried_id`) values (2,6);

SET SQL_MODE=@OLD_SQL_MODE;