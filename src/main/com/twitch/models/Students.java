package twitch.models;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "de.vogella.xml.jaxb.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Students {

    // XmLElementWrapper генерира обвиващ елемент около XML елементите, родителския елемент
    @XmlElementWrapper(name = "studentList")
    // XmlElement задава името на вътрешните елементи
    @XmlElement(name = "student")
    private ArrayList<User> students;

    public Students() { }

    public void add (User user) {
        this.students.add(user);
    }

    public void delete (String username) {
        for (int i = students.size() - 1; i >= 0; i--) {
            User usr = students.get(i);
            if (usr.getUsername().equals(username)) {
                students.remove(i);
            }
        }
    }

    public void setStudents(ArrayList<User> students) {
        this.students = students;
    }

    public ArrayList<User> getStudents() {
        return students;
    }
}
