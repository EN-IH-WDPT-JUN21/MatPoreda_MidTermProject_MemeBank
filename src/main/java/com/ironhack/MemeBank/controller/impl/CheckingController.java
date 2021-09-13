package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.accounts.Checking;
import com.ironhack.MemeBank.repository.CheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class CheckingController {


    @Autowired
    private CheckingRepository checkingRepository;

//    @Autowired
//    private CourseService courseService;


    @GetMapping("/checkings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Checking> getCheckingByIdPathVariable(@PathVariable(name = "id") Long id) {
        return checkingRepository.findById(id);
    }


//    @GetMapping("/checking")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Checking> getCoursesByAccountHolder(@RequestParam Optional<AccountHolder> accountHolder) {
//        if (accountHolder.isPresent()) {
//            return checkingRepository.findByAccountHolder(accountHolder.get());
//        }
//        else{
//            return checkingRepository.findAll();
//        }
//    }

//    @GetMapping("/checking")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Checking> getCoursesByAccountHolder(@RequestParam String accountHolderName, @RequestParam Long id ) {
//        if (!accountHolderName.isEmpty() && !id.equals(0)) {
//            return checkingRepository.findByAccountHolder(accountHolder.get());
//        }
//        else{
//            return checkingRepository.findAll();
//        }
//    }

    @GetMapping("/checkings")
    @ResponseStatus(HttpStatus.OK)
    public List<Checking> getAllCheckings() {
            return checkingRepository.findAll();
    }

//
//    @GetMapping("/checking/name")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Course> findCoursesByName(@RequestParam Optional<String> name){
//        if(name.isPresent()){
//            return courseRepository.findByName(name.get());
//        }
//        else return courseRepository.findAll();
//    }
//
////    @GetMapping("/courses/{name}")
////    @ResponseStatus(HttpStatus.OK)
////    public Course getByName(@PathVariable(name="name") String name ){
////        Optional<Course> optionalCourse = courseRepository.getByName(name);
////        return optionalCourse.isPresent() ? optionalCourse.get() : null;
////    }

    @PostMapping("/checkings")
    @ResponseStatus(HttpStatus.CREATED)
    public Checking store(@RequestBody @Valid Checking checking) {
        return checkingRepository.save(checking);
    }

//    @PutMapping("/courses/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void update_put(@PathVariable(name="id") String code, @RequestBody CourseDTO course){
//        courseService.update(code, course);
//    }
//
//    @PatchMapping("/courses/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void update_patch(@PathVariable(name="id") String code, @RequestBody CourseDTO course){
//        courseService.update(code, course);
//    }
//
//

}
