drop table if exists "lo_medal" ;
create table lo_medal(
    _id integer not null primary key,   
    _ranking integer not null,
    _picture text, 
    _simpleName text not null,
    _gold integer not null,
    _silver integer not null,
    _copper integer not null,
    _total integer not  null    
);
drop table if exists "lo_project" ;
create table lo_project(
    _id integer not null primary key,    
    _name text not null
);
drop table if exists "lo_match" ;
create table lo_match(
    _id integer not null primary key, 
    _date date not null,
    _time time not null,       
    _projectId integer not null constraint fk_project_id references lo_project(_id) on delete cascade,
    _name text not null, 
    _hasTextLive integer not null,
    _hasVideoLive integer not null 
);
drop table if exists "lo_textLive" ;
create table lo_textLive(
    _id integer not null primary key ,     
	_matchId integer not null constraint fk_match_id references lo_match(_id) on delete cascade,	
    _serverTime datetime not null,    
    _score text not null,    
    _textTime text not null,    
    _text text not null
);
drop table if exists "lo_question" ;
create table lo_question(
    _id integer not null primary key ,    
    _order integer not null,         
    _date date not null,         
    _text text not null,    
	_answerId integer not null constraint fk_answer_id references lo_answer(_id) on delete cascade,	
    _score integer not null 
);
drop table if exists "lo_answer" ;
create table lo_answer(
    _id integer not null primary key ,    
    _order integer not null,              
    _text text not null  
);
drop table if exists "lo_answer2question" ;
create table lo_answer2question( 
	_answerId integer not null constraint fk_answer_id references lo_answer(_id) on delete cascade,	
	_questionId integer not null constraint fk_question_id references lo_question(_id) on delete cascade		
);
drop table if exists "lo_user_answer" ;
create table lo_user_answer(
    _questionId integer not null,    
    _answerId integer not null,              
    _isRight integer not null  
);