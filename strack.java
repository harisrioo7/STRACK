import java.sql.*;
import java.util.Scanner;

public class strack {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/strack";
        String user = "root";
        String password = "mysql@123";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Scanner sc = new Scanner(System.in);
            int choice = 0;
            while (choice != 4) {
                try {
                    System.out.println(
                            "\n=== Centralized HEI Student Activity Platform ===\n1. Student Access (View Records)\n2. Teacher Access (Update Attendance & Marks)\n3. Admin Access (Add Student or Teacher)\n4. Logout (Exit Application)\nSelect Role (1-4): ");
                    choice = sc.nextInt();
                    if (choice == 1) {
                        StudentAccess(conn, sc);
                        break;
                    } else if (choice == 2) {
                        TeacherAccess(conn, sc);
                        break;
                    } else if (choice == 3) {
                        AdminAccess(conn, sc);
                        break;
                    } else {
                        System.out.println("Invalid option selected.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    sc.nextLine();
                }
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Error occurred while connecting to the database.\n" + e.getMessage());
        } finally {
            System.out.println("Exiting application. Goodbye!");
        }
    }

    // ---- STUDENT ACCESS ----
    public static void StudentAccess(Connection conn, Scanner sc) {
        try {
            System.out.print("\nEnter username (Student ID): ");
            int sid = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter password (DOB in YYYY-MM-DD): ");
            String dob = sc.nextLine();

            String query = "SELECT s.*, g.half_yearly_subject_1, g.half_yearly_subject_2, g.half_yearly_subject_3, " +
                    "g.half_yearly_subject_4, g.half_yearly_subject_5, g.half_yearly_subject_6, " +
                    "g.annual_subject_1, g.annual_subject_2, g.annual_subject_3, " +
                    "g.annual_subject_4, g.annual_subject_5, g.annual_subject_6 " +
                    "FROM student s LEFT JOIN grade g ON s.student_id = g.student_id " +
                    "WHERE s.student_id = ? AND s.date_of_birth = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sid);
            pstmt.setString(2, dob);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println(
                        "\n================ STUDENT PROFILE ==================\n" +
                                "ID: " + rs.getInt("student_id") + "\n" +
                                "Admission No: " + rs.getString("admission_num") + "\n" +
                                "Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + "\n" +
                                "DOB: " + rs.getDate("date_of_birth") + "\n" +
                                "Gender: " + rs.getString("gender") + "\n" +
                                "Blood Group: " + rs.getString("blood_group") + "\n" +
                                "Category: " + rs.getString("category") + "\n" +
                                "Address: " + rs.getString("address") + "\n" +
                                "Parent Name: " + rs.getString("parents_name") + "\n" +
                                "Parent Mobile: " + rs.getString("parents_mobile_num") + "\n" +
                                "Parent Email: " + rs.getString("parents_email") + "\n" +
                                "Class Section: " + rs.getString("class_name_section") + "\n" +
                                "Attendance: " + rs.getBigDecimal("overall_attendance_percentage") + "%\n" +
                                "Overall Academic Percentage: " + rs.getBigDecimal("overall_academic_percentage")
                                + "%\n" +
                                "Disciplinary Flag: " + rs.getString("disciplinary_flag") + "\n" +
                                "Status: " + rs.getString("status") + "\n" +
                                "Extra Curricular: " + rs.getString("extra_curricular_activities") + "\n" +
                                "\n---------------- ACADEMIC GRADES ----------------\n" +
                                "[Half Yearly Examinations]\n" +
                                "Subject 1: " + rs.getString("half_yearly_subject_1") + "\n" +
                                "Subject 2: " + rs.getString("half_yearly_subject_2") + "\n" +
                                "Subject 3: " + rs.getString("half_yearly_subject_3") + "\n" +
                                "Subject 4: " + rs.getString("half_yearly_subject_4") + "\n" +
                                "Subject 5: " + rs.getString("half_yearly_subject_5") + "\n" +
                                "Subject 6: " + rs.getString("half_yearly_subject_6") + "\n" +
                                "\n[Annual Examinations]\n" +
                                "Subject 1: " + rs.getString("annual_subject_1") + "\n" +
                                "Subject 2: " + rs.getString("annual_subject_2") + "\n" +
                                "Subject 3: " + rs.getString("annual_subject_3") + "\n" +
                                "Subject 4: " + rs.getString("annual_subject_4") + "\n" +
                                "Subject 5: " + rs.getString("annual_subject_5") + "\n" +
                                "Subject 6: " + rs.getString("annual_subject_6") + "\n" +
                                "=================================================");
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---- TEACHER ACCESS ----
    public static void TeacherAccess(Connection conn, Scanner sc) {
        try {
            System.out.print("\nEnter username (Staff ID): ");
            int staffId = sc.nextInt();
            sc.nextLine(); // clear buffer
            System.out.print("Enter password (DOB in YYYY-MM-DD): ");
            String dob = sc.nextLine();

            String authQuery = "SELECT first_name, last_name, class_teacher_name_sec FROM staff WHERE staff_id = ? AND date_of_birth = ?";
            PreparedStatement pstmt = conn.prepareStatement(authQuery);
            pstmt.setInt(1, staffId);
            pstmt.setString(2, dob);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String teacherName = rs.getString("first_name") + " " + rs.getString("last_name");
                String assignedClass = rs.getString("class_teacher_name_sec");
                System.out.println("\nLogin successful! Welcome, " + teacherName);
                System.out.println("Assigned Class & Section: " + assignedClass);

                int teacherChoice = 0;
                while (teacherChoice != 7) {
                    System.out.println("\n--- Teacher Dashboard ---");
                    System.out.println(
                            "1. View All Students in Assigned Class with Overall Academic Percentage, Attendance, Disciplinary Flag");
                    System.out.println("2. View Half-Yearly & Annual Marks (Grade Table)");
                    System.out.println("3. Update Overall Academic Percentage");
                    System.out.println("4. Update Student Attendance");
                    System.out.println("5. Update Student Disciplinary Flag");
                    System.out.println("6. Update Half Yearly / Annual Marks");
                    System.out.println("7. Logout");
                    System.out.print("Select an option (1-7): ");

                    teacherChoice = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    switch (teacherChoice) {
                        case 1:
                            viewClassStudents(conn, assignedClass);
                            break;
                        case 2:
                            viewHalfYearlyAnnualMarks(conn, sc, assignedClass);
                            break;
                        case 3:
                            updateOverallAcademicPercentage(conn, sc, assignedClass);
                            break;
                        case 4:
                            updateAttendance(conn, sc, assignedClass);
                            break;
                        case 5:
                            updateDisciplinaryFlag(conn, sc, assignedClass);
                            break;
                        case 6:
                            updateHalfYearlyAnnualMarks(conn, sc, assignedClass);
                            break;
                        case 7:
                            System.out.println("Logging out from Teacher Dashboard...");
                            break;
                        default:
                            System.out.println("Invalid option selected.");
                    }
                }
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while accessing the database.");
        }
    }

    // Helper 1: View All Students in Assigned Class and their Overall Academic
    // Percentage, Attendance, and Disciplinary Flag
    private static void viewClassStudents(Connection conn, String assignedClass) throws SQLException {
        String query = "SELECT student_id, first_name, last_name, overall_academic_percentage, overall_attendance_percentage, disciplinary_flag FROM student WHERE class_name_section = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, assignedClass);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("\n--- Students in Class " + assignedClass + " ---");
        System.out.printf("%-10s %-20s %-20s %-10s %-10s %-15s\n", "Student ID", "First Name", "Last Name",
                "Academic %", "Attendance %", "Disciplinary Flag");
        System.out
                .println("------------------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-10d %-20s %-20s %-10.2f %-10.2f %-15s\n",
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getBigDecimal("overall_academic_percentage"),
                    rs.getBigDecimal("overall_attendance_percentage"),
                    rs.getString("disciplinary_flag"));
        }
    }

    // Helper 2: View Student's Half-Yearly & Annual Marks
    private static void viewHalfYearlyAnnualMarks(Connection conn, Scanner sc, String assignedClass)
            throws SQLException {
        System.out.print("\nEnter Student ID: ");
        int sid = sc.nextInt();

        String query = "SELECT s.*, g.half_yearly_subject_1, g.half_yearly_subject_2, g.half_yearly_subject_3, " +
                "g.half_yearly_subject_4, g.half_yearly_subject_5, g.half_yearly_subject_6, " +
                "g.annual_subject_1, g.annual_subject_2, g.annual_subject_3, " +
                "g.annual_subject_4, g.annual_subject_5, g.annual_subject_6 " +
                "FROM student s LEFT JOIN grade g ON s.student_id = g.student_id " +
                "WHERE s.student_id = ? AND s.class_name_section = ?";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, sid);
        pstmt.setString(2, assignedClass);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println(
                    "\n================ STUDENT RECORD ==================\n" +
                            "ID: " + rs.getInt("student_id") + "\n" +
                            "Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + "\n" +
                            "Class Section: " + rs.getString("class_name_section") + "\n" +
                            "Attendance: " + rs.getBigDecimal("overall_attendance_percentage") + "%\n" +
                            "Overall Academic Percentage: " + rs.getBigDecimal("overall_academic_percentage") + "%\n" +
                            "Disciplinary Flag: " + rs.getString("disciplinary_flag") + "\n" +
                            "\n---------------- ACADEMIC GRADES ----------------\n" +
                            "[Half Yearly Examinations]\n" +
                            "Subject 1: " + rs.getString("half_yearly_subject_1") + "\n" +
                            "Subject 2: " + rs.getString("half_yearly_subject_2") + "\n" +
                            "Subject 3: " + rs.getString("half_yearly_subject_3") + "\n" +
                            "Subject 4: " + rs.getString("half_yearly_subject_4") + "\n" +
                            "Subject 5: " + rs.getString("half_yearly_subject_5") + "\n" +
                            "Subject 6: " + rs.getString("half_yearly_subject_6") + "\n" +
                            "\n[Annual Examinations]\n" +
                            "Subject 1: " + rs.getString("annual_subject_1") + "\n" +
                            "Subject 2: " + rs.getString("annual_subject_2") + "\n" +
                            "Subject 3: " + rs.getString("annual_subject_3") + "\n" +
                            "Subject 4: " + rs.getString("annual_subject_4") + "\n" +
                            "Subject 5: " + rs.getString("annual_subject_5") + "\n" +
                            "Subject 6: " + rs.getString("annual_subject_6") + "\n" +
                            "=================================================");
        } else {
            System.out
                    .println("Student ID not found or does not belong to your assigned class (" + assignedClass + ").");
        }
    }

    // Helper 3: Update Overall Academic Percentage in Student table
    private static void updateOverallAcademicPercentage(Connection conn, Scanner sc, String assignedClass)
            throws SQLException {
        System.out.print("\nEnter Student ID: ");
        int sid = sc.nextInt();
        System.out.print("Enter New Overall Academic Percentage: ");
        double academicPct = sc.nextDouble();

        String query = "UPDATE student SET overall_academic_percentage = ? WHERE student_id = ? AND class_name_section = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDouble(1, academicPct);
        pstmt.setInt(2, sid);
        pstmt.setString(3, assignedClass);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Overall academic percentage updated successfully!");
        } else {
            System.out.println("Student ID not found or does not belong to your assigned class.");
        }
    }

    // Helper 4: Update Attendance Percentage
    private static void updateAttendance(Connection conn, Scanner sc, String assignedClass) throws SQLException {
        System.out.print("\nEnter Student ID: ");
        int sid = sc.nextInt();
        System.out.print("Enter New Attendance Percentage: ");
        double attendance = sc.nextDouble();

        String query = "UPDATE student SET overall_attendance_percentage = ? WHERE student_id = ? AND class_name_section = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDouble(1, attendance);
        pstmt.setInt(2, sid);
        pstmt.setString(3, assignedClass);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Attendance updated successfully!");
        } else {
            System.out.println("Student ID not found or does not belong to your assigned class.");
        }
    }

    // Helper 5: Update Disciplinary Status Flag
    private static void updateDisciplinaryFlag(Connection conn, Scanner sc, String assignedClass) throws SQLException {
        System.out.print("\nEnter Student ID: ");
        int sid = sc.nextInt();
        sc.nextLine(); // clear buffer
        System.out.print("Enter New Disciplinary Flag ('Out Standing', 'Good', 'Bad'): ");
        String flag = sc.nextLine();

        String query = "UPDATE student SET disciplinary_flag = ? WHERE student_id = ? AND class_name_section = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, flag);
        pstmt.setInt(2, sid);
        pstmt.setString(3, assignedClass);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Disciplinary flag updated successfully!");
        } else {
            System.out.println("Student ID not found or invalid class selection.");
        }
    }

    // Helper 6: Edit Half-Yearly and Annual Marks in Grade Table
    private static void updateHalfYearlyAnnualMarks(Connection conn, Scanner sc, String assignedClass)
            throws SQLException {
        System.out.print("\nEnter Student ID: ");
        int sid = sc.nextInt();
        sc.nextLine(); // clear buffer

        System.out.println("\nSelect Exam Term to Update:");
        System.out.println("1. Half Yearly");
        System.out.println("2. Annual");
        System.out.print("Choice (1 or 2): ");
        int termChoice = sc.nextInt();

        System.out.print("Select Subject Slot Number (1-6): ");
        int slot = sc.nextInt();
        sc.nextLine(); // clear buffer

        if (slot < 1 || slot > 6) {
            System.out.println("Invalid subject slot selection.");
            return;
        }

        String targetColumn = "";
        if (termChoice == 1) {
            targetColumn = "half_yearly_subject_" + slot;
        } else if (termChoice == 2) {
            targetColumn = "annual_subject_" + slot;
        } else {
            System.out.println("Invalid term choice.");
            return;
        }

        System.out.print("Enter Subject Name and Score (e.g. 'Subject: marks'): ");
        String newValue = sc.nextLine();

        String query = "UPDATE grade SET " + targetColumn + " = ? WHERE student_id = ? AND class_name_section = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, newValue);
        pstmt.setInt(2, sid);
        pstmt.setString(3, assignedClass);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Subject marks updated successfully!");
        } else {
            System.out.println("Student ID not found in grade table or class mismatch.");
        }
    }

    // --- ADMIN ACCESS ---
    public static void AdminAccess(Connection conn, Scanner sc) {
        try {
            System.out.print("\nEnter Admin ID: ");
            String adminId = sc.next();
            sc.nextLine(); // clear buffer
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            // 1. Authenticate against admin table
            String authQuery = "SELECT * FROM admin WHERE admin_id = ? AND pass = ?";
            PreparedStatement pstmt = conn.prepareStatement(authQuery);
            pstmt.setString(1, adminId);
            pstmt.setString(2, pass);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nAdmin Authentication Successful!");
                int adminChoice = 0;
                while (adminChoice != 3) {
                    System.out.println("\n--- Admin Dashboard ---");
                    System.out.println("1. Manage Student Records");
                    System.out.println("2. Manage Staff Records");
                    System.out.println("3. Back to Main Menu");
                    System.out.print("Select choice (1-3): ");

                    adminChoice = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    switch (adminChoice) {
                        case 1:
                            manageStudents(conn, sc);
                            break;
                        case 2:
                            manageStaff(conn, sc);
                            break;
                        case 3:
                            System.out.println("Logging out from Admin Dashboard...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please select between 1-3.");
                    }
                }
            } else {
                System.out.println("Invalid Admin ID or Password!");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while accessing the database.");
        }
    }

    // --- ADMIN: STUDENT CRUD MANAGEMENT ---
    private static void manageStudents(Connection conn, Scanner sc) throws SQLException {
        int choice = 0;
        while (choice != 5) {
            System.out.println("\n--- Student Management Menu ---");
            System.out.println("1. Add New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Modify Student Record");
            System.out.println("4. Delete Student Record");
            System.out.println("5. Logout");
            System.out.print("Choice (1-5): ");
            choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1: {
                    System.out.print("Enter Student ID: ");
                    String sid = sc.nextLine();
                    System.out.print("Enter Admission Number: ");
                    String adm = sc.nextLine();
                    System.out.print("Enter First Name: ");
                    String fn = sc.nextLine();
                    System.out.print("Enter Last Name: ");
                    String ln = sc.nextLine();
                    System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                    String dob = sc.nextLine();
                    System.out.print("Enter Gender ('Male', 'Female', 'Other'): ");
                    String gender = sc.nextLine();
                    System.out.print("Enter Blood Group: ");
                    String bg = sc.nextLine();
                    System.out.print("Enter Category ('General','MBC','BC','OBC','SC','ST','EWS','Other'): ");
                    String cat = sc.nextLine();
                    System.out.print("Enter Address: ");
                    String addr = sc.nextLine();
                    System.out.print("Enter Parent's Name: ");
                    String pname = sc.nextLine();
                    System.out.print("Enter Parent's Mobile Number: ");
                    String pmob = sc.nextLine();
                    System.out.print("Enter Parent's Email: ");
                    String pemail = sc.nextLine();
                    System.out.print("Enter Class & Section (e.g. '11-A'): ");
                    String cls = sc.nextLine();
                    System.out.print("Enter Fee Due Amount: ");
                    double fee = sc.nextDouble();
                    System.out.print("Enter Extra Curricular Activities if any: ");
                    String extraCurricular = sc.nextLine();
                    sc.nextLine();

                    String insertSql = "INSERT INTO student (student_id, admission_num, first_name, last_name, date_of_birth, gender, blood_group, category, address, parents_name, parents_mobile_num, parents_email, class_name_section, fee_due_amt,extra_curricular_activities) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, sid);
                    pstmt.setString(2, adm);
                    pstmt.setString(3, fn);
                    pstmt.setString(4, ln);
                    pstmt.setString(5, dob);
                    pstmt.setString(6, gender);
                    pstmt.setString(7, bg);
                    pstmt.setString(8, cat);
                    pstmt.setString(9, addr);
                    pstmt.setString(10, pname);
                    pstmt.setString(11, pmob);
                    pstmt.setString(12, pemail);
                    pstmt.setString(13, cls);
                    pstmt.setDouble(14, fee);
                    pstmt.setString(15, extraCurricular);
                    int rows = pstmt.executeUpdate();
                    System.out.println(rows > 0 ? "Student added successfully!" : "Failed to add student.");
                    break;
                }
                case 2: {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT student_id, admission_num, first_name, last_name, class_name_section, status, extra_curricular_activities FROM student");
                    System.out.println("\n--- All Students Record List ---");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.println("ID: " + rs.getInt("student_id") +
                                " | Adm No: " + rs.getString("admission_num") +
                                " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                                " | Class: " + rs.getString("class_name_section") +
                                " | Status: " + rs.getString("status") +
                                " | Extra Curricular Activities: " + rs.getString("extra_curricular_activities"));
                    }
                    if (!found) {
                        System.out.println("No student records found.");
                    }
                    break;
                }
                case 3: {
                    System.out.print("Enter Student ID to modify: ");
                    int sid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter New Class Section: ");
                    String newCls = sc.nextLine();
                    System.out.print("Enter New Fee Due Amount: ");
                    double newFee = sc.nextDouble();
                    sc.nextLine();

                    System.out.print("Enter New Extra Curricular Activities: ");
                    String newExtraCurricular = sc.nextLine();

                    String updateSql = "UPDATE student SET class_name_section = ?, fee_due_amt = ?, extra_curricular_activities = ? WHERE student_id = ?";
                    PreparedStatement uPstmt = conn.prepareStatement(updateSql);
                    uPstmt.setString(1, newCls);
                    uPstmt.setDouble(2, newFee);
                    uPstmt.setString(3, newExtraCurricular);
                    uPstmt.setInt(4, sid);

                    int updated = uPstmt.executeUpdate();
                    System.out.println(updated > 0 ? "Student record updated successfully!" : "Student ID not found.");
                    break;
                }
                case 4: {
                    System.out.print("Enter Student ID to delete: ");
                    int delSid = sc.nextInt();
                    sc.nextLine();

                    // Optional: Delete corresponding grade records first if needed to prevent
                    // foreign key constraints
                    String delGradeSql = "DELETE FROM grade WHERE student_id = ?";
                    PreparedStatement dGPstmt = conn.prepareStatement(delGradeSql);
                    dGPstmt.setInt(1, delSid);
                    dGPstmt.executeUpdate();

                    String delSql = "DELETE FROM student WHERE student_id = ?";
                    PreparedStatement dPstmt = conn.prepareStatement(delSql);
                    dPstmt.setInt(1, delSid);

                    int deleted = dPstmt.executeUpdate();
                    System.out.println(deleted > 0 ? "Student record deleted successfully!" : "Student ID not found.");
                    break;
                }
                case 5:
                    System.out.println("Returning to Admin Dashboard...");
                    break;
                default:
                    System.out.println("Invalid option. Choose between 1-5.");
            }
        }
    }

    // --- ADMIN: STAFF CRUD MANAGEMENT ---
    private static void manageStaff(Connection conn, Scanner sc) throws SQLException {
        int choice = 0;
        while (choice != 5) {
            System.out.println("\n--- Staff Management Menu ---");
            System.out.println("1. Add New Staff Member");
            System.out.println("2. View All Staff Members");
            System.out.println("3. Modify Staff Record");
            System.out.println("4. Delete Staff Record");
            System.out.println("5. Back to Admin Dashboard");
            System.out.print("Choice (1-5): ");
            choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1: {
                    System.out.print("Enter Staff ID: ");
                    String sid = sc.nextLine();
                    System.out.print("Enter First Name: ");
                    String fn = sc.nextLine();
                    System.out.print("Enter Last Name: ");
                    String ln = sc.nextLine();
                    System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                    String dob = sc.nextLine();
                    System.out.print("Enter Gender ('Male', 'Female', 'Other'): ");
                    String gender = sc.nextLine();
                    System.out.print("Enter Blood Group: ");
                    String bg = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Phone Number: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Emergency Contact Name: ");
                    String emName = sc.nextLine();
                    System.out.print("Enter Emergency Contact Phone: ");
                    String emPhone = sc.nextLine();
                    System.out.print("Enter Address: ");
                    String addr = sc.nextLine();
                    System.out.print("Enter Assigned Class & Section (e.g. '11-A'): ");
                    String cls = sc.nextLine();
                    System.out.print("Enter Joining Date (YYYY-MM-DD): ");
                    String jdate = sc.nextLine();
                    System.out.print("Enter Basic Salary: ");
                    double salary = sc.nextDouble();
                    sc.nextLine();

                    String insertSql = "INSERT INTO staff (staff_id, first_name, last_name, date_of_birth, gender, blood_group, email, phone_number, emergency_contact_name, emergency_contact_phone, address, class_teacher_name_sec, joining_date, basic_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, sid);
                    pstmt.setString(2, fn);
                    pstmt.setString(3, ln);
                    pstmt.setString(4, dob);
                    pstmt.setString(5, gender);
                    pstmt.setString(6, bg);
                    pstmt.setString(7, email);
                    pstmt.setString(8, phone);
                    pstmt.setString(9, emName);
                    pstmt.setString(10, emPhone);
                    pstmt.setString(11, addr);
                    pstmt.setString(12, cls);
                    pstmt.setString(13, jdate);
                    pstmt.setDouble(14, salary);

                    int rows = pstmt.executeUpdate();
                    System.out.println(rows > 0 ? "Staff added successfully!" : "Failed to add staff member.");
                    break;
                }
                case 2: {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT staff_id, first_name, last_name, phone_number, class_teacher_name_sec, basic_salary FROM staff");
                    System.out.println("\n--- All Staff Record List ---");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.println("ID: " + rs.getInt("staff_id") +
                                " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                                " | Phone: " + rs.getString("phone_number") +
                                " | Class: " + rs.getString("class_teacher_name_sec") +
                                " | Salary: " + rs.getBigDecimal("basic_salary"));
                    }
                    if (!found) {
                        System.out.println("No staff records found.");
                    }
                    break;
                }
                case 3: {
                    System.out.print("Enter Staff ID to modify: ");
                    int stid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter New Assigned Class & Section: ");
                    String newCls = sc.nextLine();
                    System.out.print("Enter New Basic Salary: ");
                    double newSal = sc.nextDouble();
                    sc.nextLine();

                    String updateSql = "UPDATE staff SET class_teacher_name_sec = ?, basic_salary = ? WHERE staff_id = ?";
                    PreparedStatement uPstmt = conn.prepareStatement(updateSql);
                    uPstmt.setString(1, newCls);
                    uPstmt.setDouble(2, newSal);
                    uPstmt.setInt(3, stid);

                    int updated = uPstmt.executeUpdate();
                    System.out.println(updated > 0 ? "Staff record updated successfully!" : "Staff ID not found.");
                    break;
                }
                case 4: {
                    System.out.print("Enter Staff ID to delete: ");
                    int delStid = sc.nextInt();
                    sc.nextLine();

                    String delSql = "DELETE FROM staff WHERE staff_id = ?";
                    PreparedStatement dPstmt = conn.prepareStatement(delSql);
                    dPstmt.setInt(1, delStid);

                    int deleted = dPstmt.executeUpdate();
                    System.out.println(deleted > 0 ? "Staff record deleted successfully!" : "Staff ID not found.");
                    break;
                }
                case 5:
                    System.out.println("Returning to Admin Dashboard...");
                    break;
                default:
                    System.out.println("Invalid option. Choose between 1-5.");
            }
        }
    }
}
