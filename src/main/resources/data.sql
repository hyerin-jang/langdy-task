-- teachers
insert into teachers(id, name) values (1, 'teacher1');
insert into teachers(id, name) values (2, 'teacher2');

-- courses
insert into courses(id, name) values (1, 'course1');
insert into courses(id, name) values (2, 'course2');

-- students
insert into students(id, name, os) values (1, 'student1', 'IOS');
insert into students(id, name, os) values (2, 'student2', 'ANDROID');

-- lessons
insert into lessons(id, course_id, teacher_id, student_id, status, start_at, end_at)
values (1, 1, 1, 1, 'BOOKED', '2026-03-06T10:00:00', '2026-03-06T10:20:00');