package com.example.resources;

import com.bookislife.flow.server.domain.RoutingContextWrapper;
import com.example.model.Address;
import com.example.model.Person;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.Arrays;

@Path("/persons")
public class PersonResources {

    @GET
    @Path(":id")
    public Person getPerson(RoutingContextWrapper context) {
        System.out.println(context);
        Address homeAddress = new Address("Home", "foo");
        Address workingAddress = new Address("Working", "bar");
        Person peter = new Person();
        peter.setName("Peter");
        peter.setAge(20);
        peter.setAddressList(Arrays.asList(homeAddress, workingAddress));
        return peter;
    }

    @GET
    @Path("query/:name")
    public Person queryPerson(@Context RoutingContextWrapper context,
                              @PathParam(value = "name") String name) {
        System.out.println(context);
        System.out.println("name= " + name);
        Person person = new Person();
        person.setName(name);
        person.setAge(100);
        return person;
    }

    @GET
    @Path("find/:type")
    public Person queryPerson(@Context RoutingContextWrapper context,
                              @PathParam(value = "type") String type,
                              @QueryParam(value = "name") String name,
                              @QueryParam(value = "age") int age) {
        System.out.println("name= " + name);
        System.out.println("age= " + age);
        System.out.println("type= " + type);
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        return person;
    }

}
