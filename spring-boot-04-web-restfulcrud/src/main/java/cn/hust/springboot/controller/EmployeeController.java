package cn.hust.springboot.controller;


import cn.hust.springboot.dao.DepartmentDao;
import cn.hust.springboot.dao.EmployeeDao;
import cn.hust.springboot.entities.Department;
import cn.hust.springboot.entities.Employee;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /** 查询所有员工，返回列表页面 */
    @GetMapping("/emps")
    public String list(Model model) {
        Collection<Employee> employees = employeeDao.getAll();

        // 放在请求域中
        model.addAttribute("emps", employees);

        // thymeleaf 默认会拼接串
        // classpath:/templates/xxx.html
        return "emp/list";
    }

    /** 来到添加员工页面*/
    @GetMapping("/emp")
    public String toAddPage(Model model) {
        // 来到添加页面 查出所有的部门，在页面显示

        // Model 用于将数据封装进去给页面显示
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";
    }

    // 员工添加功能
    // SpringMVC 自动将请求参数和入参对象的属性一一绑定
    // 要求请求参数的名字和 JavaBean 入参的对象里面的属性名是一样的
    @PostMapping("/emp")
    public String addEmp(Employee employee) {
        // 来到员工列表页面
        System.out.println("保存的员工信息：" + employee);

        // 保存员工
        employeeDao.save(employee);
        // redirect 重定向到一个地址 / 代表当前项目路径
        // forward 转发
        return "redirect:/emps";
    }

    // 来到修改页面，查出当前员工，在页面回显
    @GetMapping("/emp/{id}")
    public String toEditPage(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeDao.get(id);
        model.addAttribute("emp", employee);

        // 页面要显示素有的部门列表
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);

        // 回到修改页面（add是修改添加二合一的页面）
        return "emp/add";
    }

    // 员工修改 需要提交员工 id
    @PutMapping("/emp")
    public String updateEmp(Employee employee) {
        System.out.println("员工数据：" + employee);

        employeeDao.save(employee);

        return "redirect:/emps";
    }

    // 员工删除
    @DeleteMapping("/emp/{id}")
    public String deleteEmp(@PathVariable("id") Integer id) {
        employeeDao.delete(id);

        return "redirect:/emps";
    }
}
