import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class StudentDept {

    final List<Student> studentList = new ArrayList<Student>();
    final List<Department> departmentList = new ArrayList<Department>();

    class Student {
        Integer id;
        String name;
        List<Integer> deptIds;
    }

    class Department {
        Integer departmentId;
        String deptName;
    }

    // All the departments with at least two students
    List<Department> findTwoStudentDepartments() {
        List<Department> result;
        Map<Integer, List<Student>> studentDeptMap = new HashMap<>();
        studentList.forEach(student -> {
            List<Integer> deptIds = student.deptIds;
            if (deptIds != null) {
                deptIds.forEach(deptId -> {
                    List<Student> studentAccumulator = studentDeptMap.get(deptId);
                    if (studentAccumulator == null) {
                        List<Student> newStudentAccumulator = new ArrayList<>();
                        newStudentAccumulator.add(student);
                        studentDeptMap.put(deptId, newStudentAccumulator);
                    } else {
                        studentAccumulator.add(student);
                    }
                });
            }
        });

        result = departmentList.stream().filter(department -> department.departmentId != null &&
            studentDeptMap.get(department.departmentId) != null &&
            studentDeptMap.get(department.departmentId).size() > 1).collect(Collectors.toList());
        return result;
    }


}
