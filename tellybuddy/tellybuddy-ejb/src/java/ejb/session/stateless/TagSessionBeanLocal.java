/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Tag;
import javax.ejb.Local;
import util.exception.TagNotFoundException;

/**
 *
 * @author kaikai
 */
@Local
public interface TagSessionBeanLocal {

    public Tag retrieveTagByTagId(Long tagId)throws TagNotFoundException;
    
}
