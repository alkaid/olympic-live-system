DROP TABLE IF EXISTS "lo_medal" ;
CREATE TABLE lo_medal 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _ranking int(10) NOT NULL , 
  _picture varchar(255) , 
  _simpleName varchar(255) NOT NULL , 
  _gold int(10) NOT NULL , 
  _silver int(10) NOT NULL , 
  _copper int(10) NOT NULL , 
  _total int(10) NOT NULL 
) ;
DROP TABLE IF EXISTS "lo_project" ;
CREATE TABLE lo_project 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _name varchar(255)
) ;
DROP TABLE IF EXISTS "lo_match" ;
CREATE TABLE lo_match 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _bjDate date NOT NULL , 
  _bjTime time NOT NULL , 
  _londonDate date, 
  _londonTime time, 
  _projectId int(10) NOT NULL  CONSTRAINT fk_project_id REFERENCES lo_project (_id) ON DELETE CASCADE, 
  _name varchar(255) NOT NULL,
  _videoLiveUri varchar(255),
  _hasTextLive bit(1) NOT NULL , 
  _hasVideoLive bit(1) NOT NULL,
  _videoChannel varchar(255)  
) ;
DROP TABLE IF EXISTS "lo_textLive" ;
CREATE TABLE lo_textLive 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _matchId int(10) NOT NULL CONSTRAINT fk_match_id REFERENCES lo_match (_id) ON DELETE CASCADE, 
  _serverTime datetime NOT NULL , 
  _score int(10) NOT NULL , 
  _textTime varchar(255) NOT NULL , 
  _text varchar(255) NOT NULL 
) ;
DROP TABLE IF EXISTS "lo_question" ;
CREATE TABLE lo_question 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _order int(4) NOT NULL , 
  _date date NOT NULL , 
  _text varchar(255) NOT NULL , 
  _answerId int(10) NOT NULL CONSTRAINT fk_answer_id REFERENCES lo_answer (_id) ON DELETE CASCADE, 
  _score int(10) NOT NULL 
) ;
DROP TABLE IF EXISTS "lo_answer" ;
CREATE TABLE lo_answer 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _order int(4) NOT NULL , 
  _text varchar(255) NOT NULL 
) ;
DROP TABLE IF EXISTS "lo_answer2question" ;
CREATE TABLE lo_answer2question 
( 
  _answerId int(10) NOT NULL CONSTRAINT fk_answer_id REFERENCES lo_answer (_id) ON DELETE CASCADE , 
  _questionId int(10) NOT NULL CONSTRAINT fk_question_id REFERENCES lo_question (_id) ON DELETE CASCADE 
) ;
DROP TABLE IF EXISTS "lo_user_answers" ;
CREATE TABLE lo_user_answers 
( 
  _questionId int(10) NOT NULL CONSTRAINT fk_question_id REFERENCES lo_question(_id) ON DELETE CASCADE, 
  _answerId int(10) NOT NULL CONSTRAINT fk_answer_id REFERENCES lo_answer(_id) ON DELETE CASCADE,
  _isRight int(10)    
) ;
DROP TABLE IF EXISTS "lo_user_rank" ;
CREATE TABLE lo_user_rank
(	
	_unick varchar(10) NOT NULL PRIMARY KEY ,
	_rank int(10) NOT NULL,
	_questionScore int(10) NOT NULL	 
);
INSERT INTO lo_project VALUES ('1', '蹦床');
INSERT INTO lo_project VALUES ('2', '闭幕式');
INSERT INTO lo_project VALUES ('3', '帆船');
INSERT INTO lo_project VALUES ('4', '花样游泳');
INSERT INTO lo_project VALUES ('5', '击剑');
INSERT INTO lo_project VALUES ('6', '举重');
INSERT INTO lo_project VALUES ('7', '开幕式');
INSERT INTO lo_project VALUES ('8', '篮球');
INSERT INTO lo_project VALUES ('9', '马术');
INSERT INTO lo_project VALUES ('10', '排球');
INSERT INTO lo_project VALUES ('11', '皮划艇激流回旋');
INSERT INTO lo_project VALUES ('12', '皮划艇静水');
INSERT INTO lo_project VALUES ('13', '乒乓球');
INSERT INTO lo_project VALUES ('14', '曲棍球');
INSERT INTO lo_project VALUES ('15', '拳击');
INSERT INTO lo_project VALUES ('16', '柔道');
INSERT INTO lo_project VALUES ('17', '赛艇');
INSERT INTO lo_project VALUES ('18', '沙滩排球');
INSERT INTO lo_project VALUES ('19', '射击');
INSERT INTO lo_project VALUES ('20', '射箭');
INSERT INTO lo_project VALUES ('21', '手球');
INSERT INTO lo_project VALUES ('22', '摔跤');
INSERT INTO lo_project VALUES ('23', '水球');
INSERT INTO lo_project VALUES ('24', '跆拳道');
INSERT INTO lo_project VALUES ('25', '体操');
INSERT INTO lo_project VALUES ('26', '田径');
INSERT INTO lo_project VALUES ('27', '跳水');
INSERT INTO lo_project VALUES ('28', '铁人三项');
INSERT INTO lo_project VALUES ('29', '网球');
INSERT INTO lo_project VALUES ('30', '现代五项');
INSERT INTO lo_project VALUES ('31', '艺术体操');
INSERT INTO lo_project VALUES ('32', '游泳');
INSERT INTO lo_project VALUES ('33', '羽毛球');
INSERT INTO lo_project VALUES ('34', '自行车-场地赛');
INSERT INTO lo_project VALUES ('35', '自行车-公路赛');
INSERT INTO lo_project VALUES ('36', '自行车-山地赛');
INSERT INTO lo_project VALUES ('37', '自行车-小轮车');
INSERT INTO lo_project VALUES ('38', '足球');