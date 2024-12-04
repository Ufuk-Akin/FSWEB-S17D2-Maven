package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping(path = "/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers = new HashMap<>();
    public Taxable taxable;

    // Constructor inj.
    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }


    // Proje ayağa kalkarken bu method devreye giriyor. 2 adet Developer ekledik
    @PostConstruct
    public void init() {
        System.out.println("post Construct Created");
        developers.put(1,new Developer(1,"ufuk" , 10000, Experience.JUNIOR));
        developers.put(2,new Developer(2,"ali" , 20000, Experience.MID));
    }

    @GetMapping
    public List<Developer> getDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloper(@PathVariable("id") int id) {
        if(id<0) {
            System.out.println("id must be greater than 0" + id);
            return null;
        }
        return developers.get(id);
    }

    @PostMapping
    public Developer addDeveloper(@RequestBody Developer developer) {
        double taxedSalary;
        if(developer.getExperience()==Experience.JUNIOR) {
            taxedSalary = developer.getSalary() - developer.getSalary()*taxable.getSimpleTaxRate();
            developer = new Developer(developer.getId(),developer.getName(),taxedSalary,developer.getExperience());
        } else if(developer.getExperience()==Experience.MID) {
            taxedSalary = developer.getSalary() - developer.getSalary()*taxable.getMiddleTaxRate();
            developer = new Developer(developer.getId(),developer.getName(),taxedSalary,developer.getExperience());
        } else if (developer.getExperience()==Experience.SENIOR){
            taxedSalary = developer.getSalary() - developer.getSalary()*taxable.getUpperTaxRate();
            developer = new Developer(developer.getId(),developer.getName(),taxedSalary,developer.getExperience());
        } else {
            System.out.println("wrong experience type");
        }

        developers.put(developer.getId(), developer);
        return developer;
    }

    @PutMapping("{id}")
    public Developer updateDeveloper(@PathVariable("id") int id, @RequestBody Developer developer) {
        this.developers.put(id, developer);
        return developers.get(id);
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable("id") int id) {
        System.out.println("deleting developer " + developers.get(id).getName());
        return developers.remove(id);
    }
}
