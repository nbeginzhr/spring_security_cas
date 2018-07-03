package cn.haoyu.casServer.configuration;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.jasig.services.persondir.support.StubPersonAttributeDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haoyu on 2018/6/15.
 */
public class HaoyuPersonAttributeDao extends StubPersonAttributeDao {

    @Override
    public IPersonAttributes getPerson(final String loginname) {
        if (loginname == null) {
            throw new IllegalArgumentException("Illegal to invoke getPerson(String) with a null argument.");
        }
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        attributes.put("userHid", Collections.singletonList((Object)"802349023849023480900000000999999999"));
        attributes.put("username", Collections.singletonList((Object)"802349023849023480900000000999999999"));
        attributes.put("user", Collections.singletonList((Object)"802349023849023480900000000999999999"));
        return new AttributeNamedPersonImpl(attributes);
    }


}
