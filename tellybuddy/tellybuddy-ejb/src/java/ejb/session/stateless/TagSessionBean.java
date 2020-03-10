/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Tag;
import javax.ejb.Stateless;
import util.exception.TagNotFoundException;

/**
 *
 * @author kaikai
 */
@Stateless
public class TagSessionBean implements TagSessionBeanLocal {

    @Override
    public Tag retrieveTagByTagId(Long tagId) throws TagNotFoundException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
