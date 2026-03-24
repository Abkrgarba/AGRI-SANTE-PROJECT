// STUDENT GRADE CALCULATOR

// INTERFACES (abstract classes with abstract methods in Dart)

abstract class Gradable {
  double calculateAverage();
  String getLetterGrade();
  String getRemarks();
}

abstract class Displayable {
  void displayInfo();
  void displayResult();
}

abstract class Assessable {
  void addScore(String subject, double score);
  Map<String, double> getScores();
}

// ABSTRACT BASE CLASS

abstract class Person implements Displayable {
  final String id;
  final String name;
  final int age;

  Person(this.id, this.name, this.age);

  @override
  void displayInfo() {
    print('-----------------------------------');
    print('ID     : $id');
    print('Name   : $name');
    print('Age    : $age');
  }
}

// ABSTRACT STUDENT CLASS

abstract class Student extends Person implements Gradable, Assessable {
  final int yearLevel;
  final Map<String, double> _scores = {};

  Student(String id, String name, int age, this.yearLevel)
      : super(id, name, age);

  @override
  void addScore(String subject, double score) {
    if (score < 0.0 || score > 100.0) {
      throw ArgumentError('Score must be between 0 and 100.');
    }
    _scores[subject] = score;
  }

  @override
  Map<String, double> getScores() => Map.unmodifiable(_scores);

  @override
  double calculateAverage() {
    if (_scores.isEmpty) return 0.0;
    return _scores.values.reduce((a, b) => a + b) / _scores.length;
  }

  @override
  String getLetterGrade() {
    final avg = calculateAverage();
    if (avg >= 90.0) return 'A';
    if (avg >= 80.0) return 'B';
    if (avg >= 70.0) return 'C';
    if (avg >= 60.0) return 'D';
    return 'F';
  }

  @override
  String getRemarks() {
    switch (getLetterGrade()) {
      case 'A':
        return 'Excellent';
      case 'B':
        return 'Good';
      case 'C':
        return 'Average';
      case 'D':
        return 'Below Average';
      default:
        return 'Failed';
    }
  }

  @override
  void displayInfo() {
    super.displayInfo();
    print('Year   : Year $yearLevel');
    print('Type   : ${studentType()}');
  }

  @override
  void displayResult() {
    displayInfo();
    print('-----------------------------------');
    print('Subjects & Scores:');
    if (_scores.isEmpty) {
      print('  No scores recorded.');
    } else {
      _scores.forEach((subject, score) {
        print('  ${subject.padRight(20)}: ${score.toStringAsFixed(2)}');
      });
    }
    print('-----------------------------------');
    print('Average Grade  : ${calculateAverage().toStringAsFixed(2)}');
    print('Letter Grade   : ${getLetterGrade()}');
    print('Remarks        : ${getRemarks()}');
    print('-----------------------------------\n');
  }

  String studentType();
}

// CONCRETE STUDENT SUBCLASSES

class UndergraduateStudent extends Student {
  final String major;

  UndergraduateStudent({
    required String id,
    required String name,
    required int age,
    required int yearLevel,
    required this.major,
  }) : super(id, name, age, yearLevel);

  @override
  String studentType() => 'Undergraduate';

  @override
  void displayInfo() {
    super.displayInfo();
    print('Major  : $major');
  }
}

class GraduateStudent extends Student {
  final String thesis;

  GraduateStudent({
    required String id,
    required String name,
    required int age,
    required int yearLevel,
    required this.thesis,
  }) : super(id, name, age, yearLevel);

  @override
  String studentType() => 'Graduate';

  // Graduate students need a higher bar to pass (65 instead of 60)
  @override
  String getLetterGrade() {
    final avg = calculateAverage();
    if (avg >= 90.0) return 'A';
    if (avg >= 80.0) return 'B';
    if (avg >= 70.0) return 'C';
    if (avg >= 65.0) return 'D';
    return 'F';
  }

  @override
  void displayInfo() {
    super.displayInfo();
    print('Thesis : $thesis');
  }
}

class ScholarshipStudent extends Student {
  final String scholarshipName;

  ScholarshipStudent({
    required String id,
    required String name,
    required int age,
    required int yearLevel,
    required this.scholarshipName,
  }) : super(id, name, age, yearLevel);

  @override
  String studentType() => 'Scholarship Student';

  // Must maintain at least 85 average to keep scholarship
  bool isScholarshipMaintained() => calculateAverage() >= 85.0;

  @override
  void displayResult() {
    super.displayResult();
    final status = isScholarshipMaintained()
        ? 'Scholarship Maintained'
        : 'Scholarship At Risk';
    print('Scholarship    : $scholarshipName');
    print('Status         : $status');
    print('-----------------------------------\n');
  }
}

// GRADE REPORT MANAGER

class GradeReportManager implements Displayable {
  final List<Student> _studentList = [];

  void enrollStudent(Student student) {
    _studentList.add(student);
    print(' Enrolled: ${student.name}');
  }

  void generateAllReports() {
    print('\n========== GRADE REPORT ==========\n');
    if (_studentList.isEmpty) {
      print('No students enrolled.');
      return;
    }
    for (final student in _studentList) {
      student.displayResult();
    }
  }

  Student? getTopStudent() {
    if (_studentList.isEmpty) return null;
    return _studentList.reduce((a, b) =>
        a.calculateAverage() >= b.calculateAverage() ? a : b);
  }

  @override
  void displayInfo() {
    print('Total Students Enrolled: ${_studentList.length}');
  }

  @override
  void displayResult() {
    displayInfo();
    final top = getTopStudent();
    if (top != null) {
      print(
          'Top Student: ${top.name} with average ${top.calculateAverage().toStringAsFixed(2)} (${top.getLetterGrade()})');
    }
  }
}

// MAIN

void main() {
  final manager = GradeReportManager();

  final student1 = UndergraduateStudent(
    id: 'U001',
    name: 'Alice Johnson',
    age: 20,
    yearLevel: 2,
    major: 'Computer Science',
  );
  student1.addScore('Mathematics', 92.0);
  student1.addScore('Programming', 88.0);
  student1.addScore('Data Structures', 95.0);
  student1.addScore('English', 85.0);

  final student2 = GraduateStudent(
    id: 'G001',
    name: 'Bob Martinez',
    age: 25,
    yearLevel: 1,
    thesis: 'Machine Learning in Healthcare',
  );
  student2.addScore('Advanced Algorithms', 78.0);
  student2.addScore('Research Methods', 82.0);
  student2.addScore('Statistics', 74.0);
  student2.addScore('Thesis Writing', 80.0);

  final student3 = ScholarshipStudent(
    id: 'S001',
    name: 'Clara Lee',
    age: 19,
    yearLevel: 1,
    scholarshipName: "Dean's Excellence Award",
  );
  student3.addScore('Biology', 90.0);
  student3.addScore('Chemistry', 87.0);
  student3.addScore('Physics', 83.0);
  student3.addScore('English', 91.0);

  final student4 = ScholarshipStudent(
    id: 'S002',
    name: 'David Kim',
    age: 21,
    yearLevel: 3,
    scholarshipName: 'STEM Grant',
  );
  student4.addScore('Calculus', 72.0);
  student4.addScore('Physics', 68.0);
  student4.addScore('Engineering', 75.0);
  student4.addScore('Technical Writing', 70.0);

  // Enroll all students
  print('\n========== ENROLLMENT ==========');
  manager.enrollStudent(student1);
  manager.enrollStudent(student2);
  manager.enrollStudent(student3);
  manager.enrollStudent(student4);

  // Generate full grade reports
  manager.generateAllReports();

  // Summary
  print('========== SUMMARY ==========');
  manager.displayResult();
  print('=================================\n');
}
