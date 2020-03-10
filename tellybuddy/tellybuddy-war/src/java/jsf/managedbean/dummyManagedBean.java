/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author kaikai
 */
@Named(value = "dummyManagedBean")
@Dependent
public class dummyManagedBean {

    /**
     * Creates a new instance of dummyManagedBean
     */
    public dummyManagedBean() {
    }
    
}
