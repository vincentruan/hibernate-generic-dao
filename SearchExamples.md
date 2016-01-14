## Important Notes ##
Most of the examples below reference a variable "s". If the code snippit does not initialize the variable explicitly, assume that it is initialized like this:
```
Search s = new Search();
```

Also note, when using a Generic DAO the root class that is being searched on implied by the DAO so we can simply use `new Search()`. However, when using General DAO, this is not the case. In that case, the search class is usually specified in the constructor--for example `new Search(Person.class)`.

Many methods on the `Search` class return the search object itself. This allows for chaining of methods. Thus
```
Search s = new Search().addFilterEqual("name", "Joe").addFilterLessThan("age", 21).addSortAsc("age").setMaxResults(10).setPage(2);
```
is the same as
```
Search s = new Search();
s.addFilterEqual("name", "Joe");
s.addFilterLessThan("age", 21);
s.addSortAsc("age");
s.setMaxResults(10);
s.setPage(2);
```

For more information on Search see the [Search](Search.md) page.

## Examples ##
### Filter EQUAL ###
Find all men named Bob.
```
Search s = new Search(Person.class);
s.addFilterEqual("firstName", "Bob");
s.addFilterEqual("male", true);
```

### Filter LESS\_THAN, GREATER\_THAN, LESS\_OR\_EQUAL, GREATER\_OR\_EQUAL ###
Find employees with salaries under $40,000.
```
s.addFilterLessThan("salary", 40000);
```

Find employees with salaries of at least $40,000 but less than $100,000.
```
s.addFilterGreaterOrEqual("salary", 40000).addFilterLessThan("salary", 100000);
s.setDisjunction(true); //disjunction makes it "or" rather than "and"

//another way to do this

s.addFilterOr(Filter.greaterOrEqual("salary", 40000), Filter.lessThan("salary", 100000));
```

Find children age 6 or under.
```
s.addFitlerLessOrEqual("age", 6);
```

Find companies with positive net profit.
```
s.addFilterGreaterThan("netProfit", 0);
```

### Filter LIKE, ILIKE ###
Find addresses in cities starting with "Chi".
```
s.addFilterLike("city", "Chi%");
```

Find addresses in cities starting with "chi", ignoring case.
```
s.addFilterILike("city", "chi%");
```

### Filter IN, NOT\_IN ###
Find people living in Hartford, Hereford, or Hampshire.
```
s.addFilterIn("city", "Hartford", "Hereford", "Hampshire");

//HQL equivalent...
 select p from Person p where p.city in ('Hartford', 'Hereford', 'Hampshire')
```

Find red and yellow game peices.
```
s.addFilterIn("color", "red", "yellow");
```

Find bowls of porridge that would be neither too hot nor too cold.
```
s.addFilterNotIn("temperature", "hot", "cold");
```

### Filter IS\_NULL, NOT\_NULL ###
Find tasks that have not been assigned.
```
s.addFilterNull("owner");

//HQL equivalent...
 select t from Task t where t.owner is null
```

Find lots that have a house.
```
s.addFilterNotNull("house");
```

### Filter IS\_EMPTY, NOT\_EMPTY ###
**_EMPTY with entity (same as is null)_**

Find tasks that have not been assigned.
```
s.addFilterEmpty("owner");

//HQL equivalent...
 select t from Task t where t.owner is null
```

Find lots that have a house.
```
s.addFilterNotEmpty("house");
```

**_EMPTY with string_**

Find forms where the maiden name is blank (either null or empty string).
```
s.addFilterEmpty("maidenName");

//HQL equivalent...
 select f from Form f where f.maidenName is null or f.maidenName = ''
```

Find people with the middle name specified (neither null nor empty string).
```
s.addFilterNotEmpty("middleName");

//HQL equivalent...
 select f from Form f where f.middleName is not null and f.middleName != ''
```

**_EMPTY with collection_**

Find clubs with no members.
```
s.addFilterEmpty("members");

//HQL equivalent...
 select c from Club c where not exists (select id from c.members)
```

Find parents with children.
```
s.addFilterNotEmpty("children");
```

### Filter SIZE, CLASS, ID ###
Find classes with at least 32 students.
```
s.addFilterGreaterOrEqual("students.size", 32);
```

Find Animals that are cats or dogs where Cat and Dog are subclasses of Animal.
```
Search s = new Search(Animal.class);
s.addFilterIn("class", Cat.class, Dog.class);
```

Find people whose pet is a Cat.
```
s.addFilterEqual("pet.class", "com.fully.qualified.path.Cat");
```
_Note that the value for a class can be specified as either a Class type (first example) or as a string with the fully qualified class name (second example)._

Find People with the identifier 132. Note that this works even if the identifier property of the Person class is named something other than "id".
```
s.addFilterEqual("id", 132);
```

PersonPet is a many-to-many relationship. PersonPet has a compound id which references a person and a pet. Find all people with a pet named "Casper".
```
Search s = new Search(PersonPet);
s.addFilterEqual("id.pet.name", "Casper");
s.addField("id.person"); //return the Person as a result rather than the PersonPet.
```

### Filter SOME, ALL, NONE ###
Find projects that I am a member of

- if projects.members is a list of people
```
Search s = new Search(Project.class);
s.addFilterSome("members", Filter.equal(Filter.ROOT_ENTITY, me)); //note that using Filter.ROOT_ENTITY (or just "") refers to the root entity itself
//or equivalently...
s.addFilterSome("members", Filter.equal("id", me.id));

/*English equivalent
There is at least one member where that member is equal to me.
*/

//HQL equivalent...
 select p from Project p
 where exists (from p.members m where m = :me)
```

- if projects.members is a list of ProjectMember many-to-many relationship class which in turn has an id component with "project" and "person" properties
```
Search s = new Search(Project.class);
s.addFilterSome("members", Filter.equal("id.person", me));

/*English equivalent
There is at least one member relationship where that relationship's person is equal to me.
*/

//HQL equivalent...
 where exists (from p.members pm where pm.id.person = :me)
```

Find projects that have at least one of the given members
```
Search s = new Search(Project.class);
s.addFilterSome("members", Filter.in(Filter.ROOT_ENTITY, us));

/*English equivalent
There is at least one member where that member is in the collection of people called us.
*/

//HQL equivalent... 
 where exists (from p.members where m in (:us))
```

Find projects that have all the given members
```
Search s = new Search(Project.class);
s.addFilterSome("members", Filter.equal(Filter.ROOT_ENTITY, member1));
s.addFilterSome("members", Filter.equal(Filter.ROOT_ENTITY, member2));
s.addFilterSome("members", Filter.equal(Filter.ROOT_ENTITY, member3));

/*English equivalent
There is at least one member where that member is equal to member1,
and there is at least one member where that member is equal to member2,
and there is at least one member where that member is equal to member3.
*/

//HQL equivalent...
 where exists (from p.members sub1_m where sub1_m = :member1)
 and exists (from p.members sub2_m where sub2_m = :member2)
 and exists (from p.members sub3_m where sub3_m = :member3)
```

Find projects that have only the given members
```
Search s = new Search(Project.class);
s.addFilterAll("members", Filter.in(Filter.ROOT_ENTITY, us));
 
/*English equivalent
It is true for every member that that member is in the collection of people called us.
*/

//HQL equivalent...
 where not exists (from p.members m where m not in (:us))
```

Find projects that have only members with top-secret clearance
```
Search s = new Search(Project.class);
s.addFilterAll(Filter.greaterOrEqual("clearance", TOP_SECRET));

/*English equivalent
It is true for every member that that member's clearance is greater than or equal to TOP_SECRET.
*/

//HQL equivalent... 
 where not exists (from p.members m where not (m.clearance >= :TOP_SECRET))
```

Find projects with me as primary member
```
Search s = new Search(Project.class);
s.addFilterSome("members", Filter.and( Filter.equal("id.person", me), Filter.equal("primary", true) ) );

/*English equivalent
There is at least one member where that member is equal to me and that member is primary.
*/

//HQL equivalent...
 where exists (from p.members m where m.id.person = :me and m.primary = 1 )
```

Find people who don't have any cats.
```
Search s = new Search(Person.class);
s.addFilterNone("pets",Filter.equal("class", Cat.class));
```

### Filter AND, OR, NOT ###
_These logical operator filters are used to combine other filters._

Find people born in the 1950s.
```
s.addFilterAnd(Filter.greaterOrEqual("birthYear", 1950), Filter.lessThan("birthYear", 1960));
```

Find people named John Doe or Jane Doe.
```
s.addFilterEqual("lastName", "Doe");
s.addFilterOr(Filter.equal("firstName", "Joe"), Filter.equal("firstName", "Jane"));

//although the second line could be more simply written
s.addFilterIn("firstName", "Joe", "Jane");
```

Find people who are named Bob, have a cat or who live in Florida and are at least 65.
```
s.addFilterOr(
    Filter.equal("name", "Bob"),
    Filter.some("pets", Filter.equal("class", Cat.class)),
    Filter.and(Filter.equal("address.state", "Florida"), Fitler.greaterOrEqual("age", 65))
);
```

Find people whose name does not start with "Mr."
```
s.addFilterNot(Filter.like("name", "Mr.%"));
```