/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Tag;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewTagException;
import util.exception.DeleteTagException;
import util.exception.TagNotFoundException;
import util.exception.UpdateTagException;

/**
 *
 * @author tjle2
 */
@Local
public interface TagSessionBeanLocal {

    public Long createNewTag(Tag newTag) throws CreateNewTagException;

    public Tag retrieveTagByTagId(Long tagId) throws TagNotFoundException;

    public List<Tag> retrieveAllTags();

    public void updateTag(Tag tag) throws TagNotFoundException, UpdateTagException;

    public void deleteTag(Long tagId) throws TagNotFoundException, DeleteTagException;

}
