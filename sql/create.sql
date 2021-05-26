
create table if not exists semester
(
    id    serial primary key,
    name varchar(20),
    semester_begin date,
    semester_end   date,
    unique (name,semester_begin,semester_end)

);
create table if not exists users
(
    id   integer primary key,
    firstname  varchar(20),
    lastname varchar(20),
    kind integer,--0学生，1老师
    unique (firstname,lastname,kind)
);
create table if not exists department
(
    id   serial primary key,
    name varchar(20),
    unique (name)
);
create table if not exists major
(
    id            serial primary key,
    name          varchar(20),
    department_id integer
        constraint uuu references department (id) ON DELETE cascade,
    unique (name)

);


create table if not exists student
(
    id            integer primary key
        constraint ooo references users (id) ON DELETE cascade,
    enrolled_date date,
    major_id      int
        constraint ddd references major (id) ON DELETE cascade


);
create table if not exists prerequisite(
                                           id serial primary key ,
                                           fid integer ,
                                           sid integer,
                                           kind integer,--0课1和2或
                                           unique (fid,sid,kind)
);
create table if not exists course
(
    id        varchar(20) primary key,
    name       varchar(20),
    credit     int,
    class_hour int,
    coursetype varchar(20),
    grading    varchar(20),
    prerequisite_id int constraint ppuj references prerequisite(id) ON DELETE cascade

);
create table if not exists major_course
(
    major_id         integer constraint ppppp references major(id) ON DELETE cascade,
    course_id  varchar(20) constraint ppppop references course(id) ON DELETE cascade,
    primary key (major_id,course_id)

);
create table if not exists coursesection
(
    id          serial primary key,
    semester_id int
        constraint ooi references semester(id) ON DELETE cascade,
    course_id   varchar(20)
        constraint wwwwd references course (id) ON DELETE cascade,
    totcapcity  int,
    leftcapcity int,
    conflictsection_id int[],
    unique (semester_id,course_id)
);
create table if not exists class
(
    id               serial primary key,
    instructor_id    integer
        constraint aaa references users (id) ON DELETE cascade,
    coursesection_id int
        constraint wwww references coursesection (id) ON DELETE cascade,
    class_begin            int,
    class_end              int,
    dayofweek        varchar(20),
    weeklist         int[],
    location         varchar(20),
    unique (instructor_id,coursesection_id,class_begin,class_end,dayofweek,weeklist,location)
);
create table if not exists student_grade
(
    id         serial primary key ,
    student_id integer
        constraint ii references student (id) ON DELETE cascade,
    section_id  integer
        constraint mmm references coursesection (id) ON DELETE cascade,
    kind integer,--0百分制1pf制
    unique (student_id, section_id,kind)
);

create table if not exists student_grade_hundred
(
    student_grade_id integer primary key
        constraint yyy references student_grade (id) ON DELETE cascade,
    grade            integer

);
create table if not exists student_grade_pf
(
    student_grade_id integer
        constraint ii references student_grade (id) ON DELETE cascade,
    grade            varchar(20)

);






