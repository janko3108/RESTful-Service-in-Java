package croatia.rit.edu.business;

import companydata.Department;
import companydata.DataLayer;

import java.util.List;

public class DepartmentBusiness {

    private DataLayer dataLayer = new DataLayer("jl8592");

    public Department getDepartment(String company, int deptId) {
        return dataLayer.getDepartment(company, deptId);
    }

    public List<Department> getAllDepartments(String company) {
        return dataLayer.getAllDepartment(company);
    }

    public Department updateDepartment(Department department) {
        String company = department.getCompany();
        int deptId = department.getId();
        String newDeptNo = department.getDeptNo();

        Department existingDept = dataLayer.getDepartment(company, deptId);
        if (existingDept == null) {
            throw new IllegalArgumentException("Department with ID " + deptId + " does not exist.");
        }

        List<Department> allDepartments = dataLayer.getAllDepartment(company);
        for (Department dept : allDepartments) {
            if (dept.getDeptNo().equals(newDeptNo) && dept.getId() != deptId) {
                throw new IllegalArgumentException(
                        "Department number " + newDeptNo + " already exists in company " + company + ".");
            }
        }

        return dataLayer.updateDepartment(department);
    }

    public Department insertDepartment(Department department) {
        String company = department.getCompany();
        String deptNo = department.getDeptNo();

        List<Department> allDepartments = dataLayer.getAllDepartment(company);
        for (Department dept : allDepartments) {
            if (dept.getDeptNo().equals(deptNo)) {
                throw new IllegalArgumentException(
                        "Department number " + deptNo + " already exists in company " + company + ".");
            }
        }

        return dataLayer.insertDepartment(department);
    }

    public int deleteDepartment(String company, int deptId) {
        return dataLayer.deleteDepartment(company, deptId);
    }

    public int deleteCompany(String companyName) {
        return dataLayer.deleteCompany(companyName);
    }
}
