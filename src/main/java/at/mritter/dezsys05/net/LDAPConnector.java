package at.mritter.dezsys05.net;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * This class is used to connect to the ldap service and execute commands
 *
 * @author Mathias Ritter
 */
public class LDAPConnector {

    private DirContext context;

    private String group;

    public static final Logger LOG = LogManager.getLogger(LDAPConnector.class);

    public LDAPConnector(String ip, String username, String password, String group) {
        this.group = group;
        this.connect(ip, username, password);
    }

    /**
     * This method is used to connect to the ldap server
     *
     * @param ip server ip
     * @param password password of the user account
     */
    private void connect(String ip, String username, String password) {

        Hashtable<String, Object> env = new Hashtable<>(11);

        // configure connection settings
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ip);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ",dc=nodomain,dc=com");
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // establish connection
            this.context = new InitialDirContext(env);
            LOG.info("Successfully established connection to LDAP server");
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

    }

    /**
     * Returns the description of the user's ldap group
     *
     * @return description of group
     */
    public String getGroupDescription() {
        NamingEnumeration results = this.search("dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=" + this.group + "))");
        return getAttributeValue(results, "description");
    }

    /**
     * Sets the description of the user's ldap group
     *
     * @param description description to set
     */
    public void setGroupDescription(String description) {
        updateAttribute("cn=" + this.group + ",dc=nodomain,dc=com", "description", description);
    }

    /**
     * Gets the value of the given ldap attribute
     *
     * @param namingEnum location of the attribute
     * @param attributeName name of the attribute
     * @return the attribute's value
     */
    private String getAttributeValue(NamingEnumeration namingEnum, String attributeName) {
        try {
            while (namingEnum.hasMore()) {
                SearchResult sr = (SearchResult) namingEnum.next();
                if (sr.getAttributes().get(attributeName) != null) {
                    return sr.getAttributes().get(attributeName).get().toString();
                }
            }
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

        return null;
    }

    /**
     * Updates the given attribute
     *
     * @param inDN dn
     * @param inAttribute the attribute
     * @param inValue the new value
     */
    public void updateAttribute(String inDN, String inAttribute, String inValue) {

        ModificationItem[] mods = new ModificationItem[1];
        Attribute mod0 = new BasicAttribute(inAttribute, inValue);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
        try {
            this.context.modifyAttributes(inDN, mods);
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

    }

    /**
     * Searches for a specific node
     *
     * @param inBase the base that will be searched
     * @param inFilter the search filzer
     * @return the naming enumeration found
     */
    public NamingEnumeration search(String inBase, String inFilter) {
        try {
            return this.context.search(inBase, inFilter, new SearchControls());
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
        return null;
    }


    /**
     * This method is used to disconnect from the ldap server
     */
    public void disconnect() {
        try {
            this.context.close();
        } catch (NamingException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

}
