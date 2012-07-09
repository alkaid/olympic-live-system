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
  _name varchar(255) NOT NULL 
) ;
DROP TABLE IF EXISTS "lo_match" ;
CREATE TABLE lo_match 
( 
  _id int(10) NOT NULL PRIMARY KEY , 
  _bjDate date NOT NULL , 
  _bjTime time NOT NULL , 
  _londonDate date NOT NULL , 
  _londonTime time NOT NULL , 
  _projectId int(10) NOT NULL CONSTRAINT fk_project_id REFERENCES lo_project (_id) ON DELETE CASCADE, 
  _name varchar(255) NOT NULL ,
  _videoLiveUri varchar(255) NOT NULL,
  _hasTextLive bit(1) NOT NULL , 
  _hasVideoLive bit(1) NOT NULL 
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
  _answerId varchar(255) NOT NULL CONSTRAINT fk_answer_id REFERENCES lo_answer (_id) ON DELETE CASCADE , 
  _questionId varchar(255) NOT NULL CONSTRAINT fk_question_id REFERENCES lo_question (_id) ON DELETE CASCADE 
) ;
DROP TABLE IF EXISTS "lo_user";
CREATE TABLE "lo_user" (
   _id varchar(255) NOT NULL PRIMARY KEY,
   _name varchar(255) NOT NULL ,
   _password varchar(255) NOT NULL,
   _questionScore int(11) NOT NULL default '0'
);
DROP TABLE IF EXISTS "lo_user_answer" ;
CREATE TABLE lo_user_answer 
( _id varchar(255) NOT NULL CONSTRAINT fk_user_id REFERENCES lo_user(_id) ON DELETE CASCADE ON UPDATE CASCADE,
  _questionId int(10) NOT NULL CONSTRAINT fk_question_id REFERENCES lo_question(_id) ON DELETE CASCADE ON UPDATE CASCADE, 
  _answerId int(10) NOT NULL  CONSTRAINT fk_answer_id REFERENCES lo_answer(_id) ON DELETE CASCADE ON UPDATE CASCADE,
  _isRight bit(1) NOT NULL   
) ;