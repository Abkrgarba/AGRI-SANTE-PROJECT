// STUDENT GRADE CALCULATOR 

interface Gradable {
    fun calculateAverage(): Double
    fun getLetterGrade(): String
    fun getRemarks(): String
}

interface Displayable {
    fun displayInfo()
    fun displayResult()
}

interface Assessable {
    fun addScore(subject: String, score: Double)
    fun getScores(): Map<String, Double>
}

//ABSTRACT BASE CLASS

abstract class Person(
    val id: String,
    val name: String,
    val age: Int
) : Displayable {

    override fun displayInfo() {
        println("-----------------------------------")
        println("ID     : $id")
        println("Name   : $name")
        println("Age    : $age")
    }
}

//ABSTRACT STUDENT CLASS

abstract class Student(
    id: String,
    name: String,
    age: Int,
    val yearLevel: Int
) : Person(id, name, age), Gradable, Assessable {

    protected val scores = mutableMapOf<String, Double>()

    override fun addScore(subject: String, score: Double) {
        require(score in 0.0..100.0) { "Score must be between 0 and 100." }
        scores[subject] = score
    }

    override fun getScores(): Map<String, Double> = scores.toMap()

    override fun calculateAverage(): Double {
        if (scores.isEmpty()) return 0.0
        return scores.values.sum() / scores.size
    }

    override fun getLetterGrade(): String {
        return when (calculateAverage()) {
            in 90.0..100.0 -> "A"
            in 80.0..89.99 -> "B"
            in 70.0..79.99 -> "C"
            in 60.0..69.99 -> "D"
            else            -> "F"
        }
    }

    override fun getRemarks(): String {
        return when (getLetterGrade()) {
            "A"  -> "Excellent"
            "B"  -> "Good"
            "C"  -> "Average"
            "D"  -> "Below Average"
            else -> "Failed"
        }
    }

    override fun displayInfo() {
        super.displayInfo()
        println("Year   : Year $yearLevel")
        println("Type   : ${studentType()}")
    }

    override fun displayResult() {
        displayInfo()
        println("-----------------------------------")
        println("Subjects & Scores:")
        if (scores.isEmpty()) {
            println("  No scores recorded.")
        } else {
            scores.forEach { (subject, score) ->
                println("  %-20s: %.2f".format(subject, score))
            }
        }
        println("-----------------------------------")
        println("Average Grade  : %.2f".format(calculateAverage()))
        println("Letter Grade   : ${getLetterGrade()}")
        println("Remarks        : ${getRemarks()}")
        println("-----------------------------------\n")
    }

    abstract fun studentType(): String
}

// CONCRETE STUDENT SUBCLASSES

class UndergraduateStudent(
    id: String,
    name: String,
    age: Int,
    yearLevel: Int,
    val major: String
) : Student(id, name, age, yearLevel) {

    override fun studentType() = "Undergraduate"

    override fun displayInfo() {
        super.displayInfo()
        println("Major  : $major")
    }
}

class GraduateStudent(
    id: String,
    name: String,
    age: Int,
    yearLevel: Int,
    val thesis: String
) : Student(id, name, age, yearLevel) {

    override fun studentType() = "Graduate"

    // Graduate students need a higher bar to pass (65 instead of 60)
    override fun getLetterGrade(): String {
        return when (calculateAverage()) {
            in 90.0..100.0 -> "A"
            in 80.0..89.99 -> "B"
            in 70.0..79.99 -> "C"
            in 65.0..69.99 -> "D"
            else            -> "F"
        }
    }

    override fun displayInfo() {
        super.displayInfo()
        println("Thesis : $thesis")
    }
}

class ScholarshipStudent(
    id: String,
    name: String,
    age: Int,
    yearLevel: Int,
    val scholarshipName: String
) : Student(id, name, age, yearLevel) {

    override fun studentType() = "Scholarship Student"

    // Must maintain at least 85 average to keep scholarship
    fun isScholarshipMaintained(): Boolean = calculateAverage() >= 85.0

    override fun displayResult() {
        super.displayResult()
        val status = if (isScholarshipMaintained()) "Scholarship Maintained" else "Scholarship At Risk"
        println("Scholarship    : $scholarshipName")
        println("Status         : $status")
        println("-----------------------------------\n")
    }
}

// GRADE REPORT MANAGER 

class GradeReportManager : Displayable {

    private val studentList = mutableListOf<Student>()

    fun enrollStudent(student: Student) {
        studentList.add(student)
        println(" Enrolled: ${student.name}")
    }

    fun generateAllReports() {
        println("\n========== GRADE REPORT ==========\n")
        if (studentList.isEmpty()) {
            println("No students enrolled.")
            return
        }
        studentList.forEach { it.displayResult() }
    }

    fun getTopStudent(): Student? = studentList.maxByOrNull { it.calculateAverage() }

    override fun displayInfo() {
        println("Total Students Enrolled: ${studentList.size}")
    }

    override fun displayResult() {
        displayInfo()
        val top = getTopStudent()
        if (top != null) {
            println("Top Student: ${top.name} with average %.2f (${top.getLetterGrade()})".format(top.calculateAverage()))
        }
    }
}



fun main() {

    val manager = GradeReportManager()

   
    val student1 = UndergraduateStudent(
        id = "U001", name = "Alice Johnson",
        age = 20, yearLevel = 2, major = "Computer Science"
    ).apply {
        addScore("Mathematics", 92.0)
        addScore("Programming", 88.0)
        addScore("Data Structures", 95.0)
        addScore("English", 85.0)
    }

   
    val student2 = GraduateStudent(
        id = "G001", name = "Bob Martinez",
        age = 25, yearLevel = 1, thesis = "Machine Learning in Healthcare"
    ).apply {
        addScore("Advanced Algorithms", 78.0)
        addScore("Research Methods", 82.0)
        addScore("Statistics", 74.0)
        addScore("Thesis Writing", 80.0)
    }

    
    val student3 = ScholarshipStudent(
        id = "S001", name = "Clara Lee",
        age = 19, yearLevel = 1, scholarshipName = "Dean's Excellence Award"
    ).apply {
        addScore("Biology", 90.0)
        addScore("Chemistry", 87.0)
        addScore("Physics", 83.0)
        addScore("English", 91.0)
    }

    
    val student4 = ScholarshipStudent(
        id = "S002", name = "David Kim",
        age = 21, yearLevel = 3, scholarshipName = "STEM Grant"
    ).apply {
        addScore("Calculus", 72.0)
        addScore("Physics", 68.0)
        addScore("Engineering", 75.0)
        addScore("Technical Writing", 70.0)
    }

    // Enroll all students
    println("\n========== ENROLLMENT ==========")
    manager.enrollStudent(student1)
    manager.enrollStudent(student2)
    manager.enrollStudent(student3)
    manager.enrollStudent(student4)

    // Generate full grade reports
    manager.generateAllReports()

    // Summary
    println("========== SUMMARY ==========")
    manager.displayResult()
    println("=================================\n")
}
