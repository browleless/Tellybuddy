package ejb.session.stateless;

import entity.Tag;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CreateNewTagException;
import util.exception.DeleteTagException;
import util.exception.TagNotFoundException;
import util.exception.UpdateTagException;

/**
 *
 * @author tjle2
 */
@Stateless
public class TagSessionBean implements TagSessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createNewTag(Tag newTag) throws CreateNewTagException {

        Query query = entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :inTagName");
        query.setParameter("inTagName", newTag.getName());

        if (query.getSingleResult() != null) {
            throw new CreateNewTagException("Tag with same name already exist");
        } else {
            entityManager.persist(newTag);
            entityManager.flush();

            return newTag.getTagId();
        }
    }

    @Override
    public Tag retrieveTagByTagId(Long tagId) throws TagNotFoundException {

        Tag tag = entityManager.find(Tag.class, tagId);

        if (tag != null) {
            return tag;
        } else {
            throw new TagNotFoundException("Tag ID " + tagId + " does not exist!");
        }
    }

    @Override
    public List<Tag> retrieveAllTags() {

        Query query = entityManager.createQuery("SELECT t FROM Tag t ORDER BY t.name ASC");
        List<Tag> tagEntities = query.getResultList();

        for (Tag tag : tagEntities) {
            tag.getProducts().size();
        }

        return tagEntities;
    }

    @Override
    public void updateTag(Tag tag) throws TagNotFoundException, UpdateTagException {

        if (tag.getTagId() != null) {

            Tag tagToUpdate = retrieveTagByTagId(tag.getTagId());

            Query query = entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :inName AND t.tagId <> :inTagId");
            query.setParameter("inName", tag.getName());
            query.setParameter("inTagId", tag.getTagId());

            if (!query.getResultList().isEmpty()) {
                throw new UpdateTagException("The name of the tag to be updated is duplicated");
            }

            tagToUpdate.setName(tag.getName());
        } else {
            throw new TagNotFoundException("Tag ID not provided for tag to be updated");
        }
    }

    @Override
    public void deleteTag(Long tagId) throws TagNotFoundException, DeleteTagException {

        Tag tagToRemove = retrieveTagByTagId(tagId);

        if (!tagToRemove.getProducts().isEmpty()) {
            throw new DeleteTagException("Tag ID " + tagId + " is associated with existing products and cannot be deleted!");
        } else {
            entityManager.remove(tagToRemove);
        }
    }
}
