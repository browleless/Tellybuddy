package ws.datamodel;

import entity.Tag;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllTagsRsp {

    private List<Tag> tags;

    public RetrieveAllTagsRsp() {
    }

    public RetrieveAllTagsRsp(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
