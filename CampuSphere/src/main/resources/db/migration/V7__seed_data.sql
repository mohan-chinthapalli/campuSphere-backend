-- ============================================================
-- V7: Seed / Demo Data
-- PostgreSQL-compatible (Supabase)
-- DEMO ONLY — Do NOT use in production
-- All passwords are BCrypt hashed versions of "Demo@1234"
-- Uses OVERRIDING SYSTEM VALUE to insert explicit IDs into
-- GENERATED ALWAYS AS IDENTITY columns.
-- ============================================================

-- Demo Users
INSERT INTO users (id, email, name, password, role, active) OVERRIDING SYSTEM VALUE VALUES
(1,  'admin@campusphere.edu',   'Admin User',        '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'ADMIN',   TRUE),
(2,  'student@campusphere.edu', 'Aarav Sharma',      '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE),
(3,  'faculty@campusphere.edu', 'Dr. Priya Menon',   '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'FACULTY', TRUE),
(4,  's2@campusphere.edu',      'Riya Patel',        '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE),
(5,  's3@campusphere.edu',      'Arjun Nair',        '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE),
(6,  'f2@campusphere.edu',      'Prof. Suresh Kumar','$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'FACULTY', TRUE),
(7,  'f3@campusphere.edu',      'Dr. Anita Rao',     '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'FACULTY', TRUE),
(8,  's4@campusphere.edu',      'Meera Joshi',       '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE),
(9,  's5@campusphere.edu',      'Kiran Desai',       '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE),
(10, 's6@campusphere.edu',      'Vikram Singh',      '$2a$12$K8q3o7RHRbOJl/hTJaBxzuTOBv5b3PGJnc.RjT1jb8mPLW3W6FxQK', 'STUDENT', TRUE);

-- Advance the identity sequence past the seeded IDs
SELECT setval(pg_get_serial_sequence('users', 'id'), 10);

-- Student Profiles (use academic_year — matches entity @Column(name="academic_year"))
INSERT INTO student_profiles (user_id, roll_number, branch, academic_year, semester, cgpa, attendance, credits, bio) VALUES
(2,  'CS22B1043', 'Computer Science and Engineering', 'Third Year · Semester 6', 6, 8.74, 87.5, 162,
 'Passionate about AI/ML and full-stack development. Building CampuSphere as final year project.'),
(4,  'CS22B1044', 'Computer Science and Engineering', 'Third Year · Semester 6', 6, 8.90, 92.0, 162,
 'Interested in cloud computing and DevOps.'),
(5,  'EC22B1021', 'Electronics and Communication',    'Third Year · Semester 6', 6, 8.20, 85.0, 155,
 'Passionate about embedded systems and IoT.'),
(8,  'CS22B1045', 'Computer Science and Engineering', 'Second Year · Semester 4', 4, 9.10, 95.0, 108,
 'Interested in competitive programming.'),
(9,  'ME22B1012', 'Mechanical Engineering',           'Third Year · Semester 6', 6, 7.80, 80.0, 148,
 'Robotics enthusiast.'),
(10, 'CS22B1046', 'Computer Science and Engineering', 'Final Year · Semester 8',  8, 8.50, 88.0, 210,
 'Final year student exploring entrepreneurship.');

-- Student Skills
INSERT INTO student_skills (user_id, skill) VALUES
(2, 'React'), (2, 'Spring Boot'), (2, 'Python'), (2, 'SQL'), (2, 'Docker'),
(4, 'Cloud'), (4, 'Kubernetes'), (4, 'Go'), (4, 'Terraform'),
(5, 'C++'), (5, 'MATLAB'), (5, 'Embedded C'), (5, 'RTOS'),
(10, 'Node.js'), (10, 'React'), (10, 'MongoDB'), (10, 'Business Strategy');

-- Faculty Profiles
INSERT INTO faculty_profiles (user_id, title, department, office, office_hours, bio, publications, citations, student_count) VALUES
(3,  'Associate Professor', 'Computer Science', 'Block A, Room 201', 'Mon-Fri 2PM–4PM',
 'Specialist in AI/ML with 10+ years of industry experience.', 28, 450, 120),
(6,  'Professor',           'Computer Science', 'Block A, Room 305', 'Tue-Thu 10AM–12PM',
 'Expert in algorithms and data structures. 15 years of teaching experience.', 45, 900, 200),
(7,  'Assistant Professor', 'Electronics',      'Block B, Room 110', 'Mon-Wed 3PM–5PM',
 'Research interests in signal processing and embedded systems.', 12, 180, 80);

-- Campus Places
INSERT INTO campus_places (slug, name, type, floors, open_hours, map_x, map_y, walk_time) VALUES
('block-a',      'Block A (Academic)',   'Academic',   4, '8AM–8PM',  30.0, 25.0, '2 min'),
('block-c',      'Block C (Labs)',       'Lab',        3, '8AM–10PM', 55.0, 25.0, '5 min'),
('library',      'Central Library',      'Library',    3, '8AM–10PM', 42.0, 45.0, '4 min'),
('amphitheatre', 'Amphitheatre',         'Event Space',1, 'Open',     25.0, 60.0, '6 min'),
('sports',       'Sports Complex',       'Sports',     2, '6AM–9PM',  70.0, 60.0, '8 min'),
('food-court',   'Food Court',           'Dining',     1, '7AM–10PM', 45.0, 70.0, '5 min'),
('incubation',   'Incubation Centre',    'Innovation', 2, '9AM–7PM',  62.0, 40.0, '7 min'),
('hostel',       'Hostel Circle',        'Residential',5, '24/7',     15.0, 75.0, '10 min');

-- Subjects (Semesters 1–8)
INSERT INTO subjects (code, name, emoji, semester, faculty_id) VALUES
('MA101', 'Engineering Mathematics I',         '📐', 1, NULL),
('PH101', 'Engineering Physics',               '⚛️', 1, NULL),
('CH101', 'Engineering Chemistry',             '🧪', 1, NULL),
('CS101', 'Introduction to Programming',       '💻', 1, 6),
('MA201', 'Engineering Mathematics II',        '📐', 2, NULL),
('EE201', 'Basic Electrical Engineering',      '⚡', 2, NULL),
('ME201', 'Engineering Mechanics',             '⚙️', 2, NULL),
('CS201', 'Data Structures',                   '🌳', 2, 6),
('MA301', 'Discrete Mathematics',              '🔢', 3, NULL),
('CS301', 'Object-Oriented Programming',       '☕', 3, 6),
('CS302', 'Computer Organization',             '🖥️', 3, NULL),
('EE301', 'Digital Electronics',               '💡', 3, 7),
('CS401', 'Design and Analysis of Algorithms', '📊', 4, 6),
('CS402', 'Operating Systems',                 '🐧', 4, NULL),
('CS403', 'Database Management Systems',       '🗄️', 4, 3),
('MA401', 'Probability and Statistics',        '📈', 4, NULL),
('CS501', 'Computer Networks',                 '🌐', 5, NULL),
('CS502', 'Software Engineering',              '🏗️', 5, 3),
('CS503', 'Theory of Computation',             '🤖', 5, NULL),
('CS504', 'Web Technologies',                  '🕸️', 5, 6),
('CS601', 'Machine Learning',                  '🤖', 6, 3),
('CS602', 'Cloud Computing',                   '☁️', 6, 6),
('CS603', 'Information Security',              '🔒', 6, NULL),
('CS604', 'Mobile Application Development',   '📱', 6, NULL),
('CS701', 'Deep Learning',                     '🧠', 7, 3),
('CS702', 'Distributed Systems',               '🔗', 7, NULL),
('CS703', 'Natural Language Processing',       '💬', 7, 3),
('CS704', 'Project Work I',                    '🔨', 7, NULL),
('CS801', 'Advanced Algorithms',               '⚡', 8, 6),
('CS802', 'Research Methodology',              '📋', 8, NULL),
('CS803', 'Elective: AI Ethics',               '⚖️', 8, 3),
('CS804', 'Project Work II',                   '🚀', 8, NULL);

-- Learning Materials
INSERT INTO learning_materials (material_key, title, material_type, subject_code, semester, author, file_size, emoji) VALUES
('m-6-CS601-NOTES',       'Machine Learning - Chapter Notes',        'NOTES',       'CS601', 6, 'Dr. Priya Menon',    '2.4 MB', '📝'),
('m-6-CS601-PDF',         'Introduction to ML - Complete Guide',     'PDF',         'CS601', 6, 'Dr. Priya Menon',    '4.8 MB', '📄'),
('m-6-CS601-VIDEO',       'ML Fundamentals - Video Lecture',         'VIDEO',       'CS601', 6, 'Dr. Priya Menon',    '2h 15m', '🎥'),
('m-6-CS601-PAPER',       'Neural Networks Survey Paper',            'PAPER',       'CS601', 6, 'IEEE',               '1.2 MB', '🔬'),
('m-6-CS601-HANDWRITTEN', 'ML Handwritten Notes (Prof)',             'HANDWRITTEN', 'CS601', 6, 'Dr. Priya Menon',    '3.1 MB', '✍️'),
('m-6-CS601-LAB_MANUAL',  'ML Lab Manual - Sem 6',                   'LAB_MANUAL',  'CS601', 6, 'Dept. of CS',        '1.8 MB', '🔬'),
('m-6-CS601-SYLLABUS',    'CS601 Machine Learning - Syllabus',       'SYLLABUS',    'CS601', 6, 'Dept. of CS',        '0.3 MB', '📋'),
('m-6-CS602-NOTES',       'Cloud Computing - Chapter Notes',         'NOTES',       'CS602', 6, 'Prof. Suresh Kumar', '2.1 MB', '📝'),
('m-6-CS602-PDF',         'AWS Architecture Guide',                  'PDF',         'CS602', 6, 'AWS',                '5.2 MB', '☁️'),
('m-6-CS602-VIDEO',       'Cloud Native Development Lecture',        'VIDEO',       'CS602', 6, 'Prof. Suresh Kumar', '1h 45m', '🎥'),
('m-6-CS603-NOTES',       'Information Security - Notes',            'NOTES',       'CS603', 6, 'Dept. of CS',        '1.9 MB', '📝'),
('m-6-CS603-PDF',         'Cryptography & Network Security',         'PDF',         'CS603', 6, 'Forouzan',           '6.4 MB', '🔒'),
('m-6-CS604-NOTES',       'Mobile Development - React Native',       'NOTES',       'CS604', 6, 'Dept. of CS',        '2.0 MB', '📱'),
('m-4-CS401-NOTES',       'Algorithm Design - Notes',                'NOTES',       'CS401', 4, 'Prof. Suresh Kumar', '2.6 MB', '📊'),
('m-4-CS401-PDF',         'CLRS - Introduction to Algorithms',       'PDF',         'CS401', 4, 'CLRS',               '8.5 MB', '📚'),
('m-4-CS403-NOTES',       'DBMS - Complete Notes',                   'NOTES',       'CS403', 4, 'Dr. Priya Menon',    '3.2 MB', '🗄️'),
('m-4-CS403-PDF',         'Database System Concepts',                'PDF',         'CS403', 4, 'Silberschatz',       '7.1 MB', '📚'),
('m-7-CS701-NOTES',       'Deep Learning - Lecture Notes',           'NOTES',       'CS701', 7, 'Dr. Priya Menon',    '3.8 MB', '🧠'),
('m-7-CS703-NOTES',       'NLP - Text Processing Notes',             'NOTES',       'CS703', 7, 'Dr. Priya Menon',    '2.9 MB', '💬');

-- Events
INSERT INTO events (slug, title, category, tagline, description, prize, event_date, starts_at, display_time, venue, seats_total, organizer_name, organizer_club, organizer_email, gradient, emoji, tags) VALUES
('hackspire-2026', 'HackSpire 2026', 'Hackathon',
 'Build the future in 24 hours',
 'HackSpire is our annual 24-hour hackathon where students build innovative solutions to real-world problems.',
 '₹50,000 prize pool', 'Aug 07, 2026', '2026-08-07 09:00:00+00', '09:00 AM – 09:00 AM',
 'Main Auditorium + Labs', 200, 'Aarav Sharma', 'Coding Club', 'coding@campusphere.edu',
 'from-violet-500 to-purple-600', '💻', 'hackathon,coding,innovation'),
('cultural-night-2026', 'Cultural Night 2026', 'Cultural',
 'An evening of art, music & dance',
 'Cultural Night is the annual celebration of diversity with performances from all departments.',
 NULL, 'Aug 15, 2026', '2026-08-15 18:00:00+00', '06:00 PM – 10:00 PM',
 'Amphitheatre', 500, 'Cultural Council', 'Cultural Council', 'cultural@campusphere.edu',
 'from-orange-400 to-rose-500', '🎭', 'cultural,music,dance'),
('ai-summit-2026', 'AI Summit 2026', 'Tech Talk',
 'The future of AI in industry',
 'A full-day summit featuring industry leaders and researchers discussing the latest in AI and ML.',
 NULL, 'Sep 01, 2026', '2026-09-01 09:30:00+00', '09:30 AM – 06:00 PM',
 'Seminar Hall', 150, 'Dr. Priya Menon', 'AI Research Club', 'ai@campusphere.edu',
 'from-blue-500 to-cyan-400', '🤖', 'ai,technology,research'),
('startup-pitch-2026', 'Startup Pitch Day', 'Entrepreneurship',
 'Pitch your startup idea to investors',
 'Present your startup ideas to a panel of investors and industry mentors.',
 '₹2,00,000 seed funding', 'Sep 15, 2026', '2026-09-15 10:00:00+00', '10:00 AM – 05:00 PM',
 'Incubation Centre', 80, 'E-Cell CampuSphere', 'E-Cell', 'ecell@campusphere.edu',
 'from-green-500 to-emerald-400', '🚀', 'startup,entrepreneurship,funding'),
('photowalk-2026', 'Campus Photo Walk', 'Workshop',
 'Capture beauty through your lens',
 'A guided photography walk around campus learning composition, lighting, and storytelling.',
 NULL, 'Aug 25, 2026', '2026-08-25 07:00:00+00', '07:00 AM – 10:00 AM',
 'Campus-wide', 40, 'Photography Society', 'Photography Society', 'photo@campusphere.edu',
 'from-amber-400 to-yellow-300', '📸', 'photography,workshop,art'),
('sports-meet-2026', 'Annual Sports Meet', 'Sports',
 'Championship awaits — give it your all',
 'The annual inter-department sports meet featuring cricket, football, basketball and more.',
 '₹10,000 winning team', 'Oct 05, 2026', '2026-10-05 08:00:00+00', '08:00 AM onwards',
 'Sports Complex', 600, 'Sports Committee', 'Sports Committee', 'sports@campusphere.edu',
 'from-sky-500 to-blue-400', '🏆', 'sports,athletics,competition');

-- Clubs
INSERT INTO clubs (slug, name, category, tagline, about, mission, member_count, recruiting, coordinator_id, gradient, emoji) VALUES
('coding-club',       'Coding Club',          'Technology', 'Where code meets creativity',
 'The Coding Club is the hub for all things programming.',
 'To foster a culture of problem-solving and innovation through programming.',
 142, TRUE, 6, 'from-violet-500 to-purple-600', '💻'),
('robotics-society',  'Robotics Society',     'Technology', 'Building the bots of tomorrow',
 'We design and build robots, from line-following bots to autonomous drones.',
 'To advance robotics education and build real-world automation solutions.',
 87, TRUE, 7, 'from-blue-500 to-cyan-400', '🤖'),
('cultural-council',  'Cultural Council',     'Arts',       'Celebrating diversity through art',
 'The Cultural Council organizes all cultural events on campus.',
 'To preserve and celebrate the rich cultural diversity of our student body.',
 210, FALSE, 3, 'from-orange-400 to-rose-500', '🎭'),
('ecell',             'E-Cell',               'Business',   'Fueling the entrepreneurial spirit',
 'The Entrepreneurship Cell connects students with investors and resources.',
 'To build a thriving startup ecosystem within the campus.',
 95, TRUE, 3, 'from-green-500 to-emerald-400', '🚀'),
('photography-society','Photography Society', 'Arts',       'See the world through a different lens',
 'We explore photography as an art form.',
 'To develop the photographic eye and storytelling ability in students.',
 63, TRUE, NULL, 'from-amber-400 to-yellow-300', '📸'),
('debate-literary',   'Debate & Literary Club','Literary',  'The power of words, the art of persuasion',
 'We conduct debate tournaments, creative writing workshops, and spoken word events.',
 'To sharpen communication, critical thinking, and literary appreciation.',
 78, FALSE, NULL, 'from-rose-500 to-pink-400', '📚');

-- Club Leads
INSERT INTO club_leads (club_id, name, role) VALUES
(1, 'Aarav Sharma',  'President'),
(1, 'Riya Patel',    'Vice President'),
(1, 'Arjun Nair',    'Technical Lead'),
(2, 'Kiran Desai',   'President'),
(2, 'Meera Joshi',   'Build Lead'),
(3, 'Vikram Singh',  'Chairperson'),
(4, 'Vikram Singh',  'President'),
(5, 'Arjun Nair',    'President');

-- Club Achievements
INSERT INTO club_achievements (club_id, achievement, sort_order) VALUES
(1, '1st Place — National Hackathon 2025', 1),
(1, 'Best Club Award 2024–25', 2),
(1, '200+ students placed through club projects', 3),
(2, 'National Robotics Championship Runners-up 2025', 1),
(2, 'Designed autonomous campus security bot', 2),
(3, 'Best Cultural Event — Inter-University 2024', 1),
(4, '3 startups incubated with VC funding', 1),
(4, 'Organized Startup Pitch Day with ₹50L in funding opportunities', 2),
(5, 'Annual Photo Exhibition winner — National College Contest 2025', 1);

-- Skill Sessions
INSERT INTO skill_sessions (slug, title, faculty_id, department, category, level, total_sessions, duration, enrolled_count, rating, schedule, emoji, gradient) VALUES
('applied-ai',       'Applied AI & ML',              3, 'Computer Science', 'AI',              'INTERMEDIATE', 12, '90 min',  48, 4.9, 'Mon & Wed · 4:00 PM', '🤖', 'from-violet-500 to-purple-600'),
('fullstack-web',    'Full-Stack Web Dev',            6, 'Computer Science', 'Web Development', 'BEGINNER',     16, '120 min', 63, 4.8, 'Tue & Thu · 3:00 PM', '🌐', 'from-blue-500 to-cyan-400'),
('dsa-mastery',      'DSA Mastery',                   6, 'Computer Science', 'DSA',             'INTERMEDIATE', 20, '90 min',  89, 4.9, 'Mon-Fri · 5:00 PM',   '📊', 'from-orange-400 to-rose-500'),
('cloud-native',     'Cloud Native Development',      6, 'Computer Science', 'Cloud',           'ADVANCED',      8, '120 min', 31, 4.7, 'Sat · 10:00 AM',      '☁️', 'from-green-500 to-emerald-400'),
('photography-art',  'Photography as Art',         NULL, 'Fine Arts',        'Photography',     'BEGINNER',      6, '180 min', 27, 4.6, 'Sun · 7:00 AM',       '📸', 'from-amber-400 to-yellow-300'),
('research-methods', 'Research Methods',              7, 'Electronics',      'Research',        'ADVANCED',     10, '90 min',  19, 4.8, 'Tue · 2:00 PM',       '🔬', 'from-rose-500 to-pink-400'),
('public-speaking',  'Public Speaking',            NULL, 'Humanities',       'Communication',   'BEGINNER',      8, '60 min',  55, 4.7, 'Thu · 5:00 PM',       '🎤', 'from-sky-500 to-blue-400'),
('professional-comm','Professional Communication', NULL, 'Humanities',       'Communication',   'INTERMEDIATE',  6, '90 min',  42, 4.5, 'Fri · 4:00 PM',       '💼', 'from-teal-500 to-cyan-400');

-- Skill Session Outcomes
INSERT INTO skill_session_outcomes (session_id, outcome, sort_order) VALUES
(1, 'Build ML models using scikit-learn and PyTorch', 1),
(1, 'Understand neural network architectures', 2),
(1, 'Deploy models using Flask/FastAPI', 3),
(2, 'Build full-stack apps with React + Node.js', 1),
(2, 'Design REST APIs and integrate databases', 2),
(2, 'Deploy to cloud platforms', 3),
(3, 'Solve LeetCode Hard problems confidently', 1),
(3, 'Master dynamic programming and graph algorithms', 2),
(3, 'Ace technical coding interviews', 3),
(4, 'Design cloud-native microservices on AWS', 1),
(4, 'Implement CI/CD pipelines', 2),
(4, 'Work with containers and Kubernetes', 3);

-- Mentor Profiles
INSERT INTO mentor_profiles (user_id, headline, available, total_sessions, avg_rating, response_time) VALUES
(10, 'Final Year CS | Full-Stack Dev & AI Enthusiast', TRUE,  24, 4.9, '~2h'),
(5,  'Electronics | Embedded Systems & IoT Expert',   TRUE,  18, 4.8, '~4h'),
(9,  'Mechanical | Robotics & CAD Specialist',         FALSE, 12, 4.7, '~6h'),
(8,  'CS | Competitive Programming Expert',            TRUE,  31, 4.9, '~1h');

-- Announcements
INSERT INTO announcements (title, body, author, tag, priority, created_by) VALUES
('End Semester Examination Schedule Released',
 'The examination schedule for Semester 6 has been released. Hall tickets will be available next week.',
 'Examination Cell', 'ACADEMICS', 'HIGH', 1),
('New Books Added to Central Library',
 'Over 500 new books across engineering, science, and humanities have been added to the library.',
 'Central Library', 'CAMPUS', 'NORMAL', 1),
('Campus Placement Drive — TCS & Infosys',
 'TCS and Infosys will conduct on-campus placement drives on September 10th and 11th.',
 'Training & Placement', 'PLACEMENTS', 'HIGH', 1),
('Research Grant Applications Open',
 'Applications are open for NSF undergraduate research grants. Deadline: September 30th.',
 'Research & Development Cell', 'RESEARCH', 'NORMAL', 1),
('Campus Wi-Fi Upgrade — Scheduled Maintenance',
 'Campus Wi-Fi will undergo maintenance on August 24th from 2AM–6AM.',
 'IT Infrastructure', 'CAMPUS', 'NORMAL', 1);

-- Sample Notifications for demo student
INSERT INTO notifications (user_id, title, body, notification_type, is_read) VALUES
(2, 'HackSpire 2026 Registration Open', 'Registration is now open for HackSpire 2026. Seats are filling fast!', 'EVENT',        FALSE),
(2, 'New material added: ML Notes',     'Dr. Priya Menon has uploaded new chapter notes for Machine Learning.', 'ACADEMIC',     FALSE),
(2, 'Coding Club: New Event Announced', 'Coding Club has announced the Algorithm Sprint. Check it out!',        'CLUB',         TRUE),
(2, 'Exam Schedule Released',           'End semester examination schedule has been released.',                  'ANNOUNCEMENT', FALSE),
(2, 'Mentorship Request Accepted',      'Vikram Singh has accepted your mentorship request.',                    'MENTORSHIP',   TRUE);

-- Timetable for demo student (Semester 6, Monday=1)
INSERT INTO timetable_entries (user_id, course_code, course_name, class_time, room, faculty_id, day_of_week, sort_order) VALUES
(2, 'CS601', 'Machine Learning',       '09:00 AM', 'A-301', 3,    1, 1),
(2, 'CS602', 'Cloud Computing',        '11:00 AM', 'A-302', 6,    1, 2),
(2, 'CS603', 'Information Security',   '02:00 PM', 'A-201', NULL, 1, 3),
(2, 'CS604', 'Mobile App Development', '09:00 AM', 'Lab-2', NULL, 2, 1),
(2, 'CS601', 'Machine Learning Lab',   '02:00 PM', 'Lab-1', 3,    2, 2),
(2, 'CS602', 'Cloud Computing',        '11:00 AM', 'A-302', 6,    3, 1),
(2, 'CS603', 'Information Security',   '09:00 AM', 'A-201', NULL, 3, 2),
(2, 'CS604', 'Mobile App Development', '02:00 PM', 'Lab-3', NULL, 4, 1),
(2, 'CS601', 'Machine Learning',       '11:00 AM', 'A-301', 3,    4, 2),
(2, 'CS602', 'Cloud Computing Lab',    '09:00 AM', 'Lab-4', 6,    5, 1);

-- Deadlines for demo student
INSERT INTO deadlines (user_id, title, course_code, due_at, urgency) VALUES
(2, 'ML Assignment 3 — CNN Implementation',     'CS601', '2026-08-20 23:59:00+00', 'HIGH'),
(2, 'Cloud Computing Lab Report',               'CS602', '2026-08-22 23:59:00+00', 'MEDIUM'),
(2, 'Information Security Quiz 2',              'CS603', '2026-08-25 10:00:00+00', 'HIGH'),
(2, 'Mobile App UI Prototype Submission',        'CS604', '2026-08-28 23:59:00+00', 'MEDIUM'),
(2, 'Research Paper Review — NLP Survey',        'CS703', '2026-09-05 23:59:00+00', 'LOW');
