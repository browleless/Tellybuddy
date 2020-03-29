/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author tjle2
 */
@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.restful.AnnouncementResource.class);
        resources.add(ws.restful.CategoryResource.class);
        resources.add(ws.restful.CustomerResource.class);
        resources.add(ws.restful.DiscountCodeResource.class);
        resources.add(ws.restful.PhoneNumberResource.class);
        resources.add(ws.restful.PlanResource.class);
        resources.add(ws.restful.ProductResource.class);
        resources.add(ws.restful.TagResource.class);
    }
    
}
