package hudson.tasks.junit;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.util.XStream2;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class TestResultActionTest {
    
    @Test
    @SuppressWarnings({"deprecation" })
    public void testDataShouldBeMigratedToAbstractTestResultActionOnDeserialization() throws IOException, ClassNotFoundException {
        
        // create a TestResultAction and fill the deprecated field 'testData':
        AbstractBuild build = getBuild();
        TestResult result = new TestResult();
        TestResultAction tra = new TestResultAction(build, result, null);
        tra.testData = new ArrayList<TestResultAction.Data>();
        tra.testData.add(new Data());
        
        // serialize and deserialize it:
        StringWriter sw = new StringWriter();
        XStream2 xStream = new XStream2();
        ObjectOutputStream oos = xStream.createObjectOutputStream(sw);
        oos.writeObject(tra);
        oos.flush();
        
        StringReader sr = new StringReader(sw.toString());
        ObjectInputStream ois = xStream.createObjectInputStream(sr);
        TestResultAction deserialized = (TestResultAction) ois.readObject();
        
        // check that 'testData' was migrated to the superclass AbstractTestResultAction:
        Assert.assertNull(deserialized.testData);
        
        List<TestAction> actions = deserialized.getActions(null);
        Assert.assertEquals(1, actions.size());
        TestAction action = actions.get(0);
        
        Assert.assertEquals("foo", action.getUrlName());
    }
    
    private AbstractBuild getBuild() throws IOException {
        
        final FreeStyleProject proj = new FreeStyleProject(new MockItemGroup(), "project");
        FreeStyleBuild build = new FreeStyleBuild(proj) {

            @Override
            public File getRootDir() {
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                return new File(tempDir, proj.getName());
            }
            
        };
        return build;
    }

    private static class Data extends hudson.tasks.junit.TestResultAction.Data {

        @SuppressWarnings("deprecation")
        @Override
        public List<? extends TestAction> getTestAction(TestObject testObject) {
            return Collections.singletonList(new TestAction() {
                
                @Override
                public String getUrlName() {
                    return "foo";
                }
                
                @Override
                public String getIconFileName() {
                    return "bar";
                }
                
                @Override
                public String getDisplayName() {
                    return null;
                }
            });
        }
        
    }
    
    private static class MockItemGroup implements ItemGroup {

        @Override
        public File getRootDirFor(Item child) {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            
            return new File(tempDir, child.getName());
        }
        
        @Override
        public File getRootDir() {
            return null;
        }

        @Override
        public void save() throws IOException {
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getFullName() {
            return null;
        }

        @Override
        public String getFullDisplayName() {
            return null;
        }

        @Override
        public Collection getItems() {
            return null;
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public String getUrlChildPrefix() {
            return null;
        }

        @Override
        public Item getItem(String name) {
            return null;
        }

        @Override
        public void onRenamed(Item item, String oldName, String newName)
                throws IOException {
        }

        @Override
        public void onDeleted(Item item) throws IOException {
        }
    }

}
