# Custom expressions in fields, filters, sorts #
What will we do about type conversion for filter expressions. For example if long is required but int is passed?

Will we be able to reference the root entity from a subquery, ex:
```
from Project p
where exists (from Project p2 where p2.anchor = p)
```
I think so...
```
Search s = new Search(Project.class);
search.addFilterAdHoc("exists (from Project p2 where p2.anchor = {})");
```

# Security #
It seems that doing queries the way we do, no amount of HQL injection will make it possible to use a search to alter database data.

The only concern is ability to query any table. I think Hibernate may provide a way to restrict table access per user roles. This may be something to look into.

Problem is we may want to do a query that accesses a table, but we need to restrict which rows are accessible to a particular user. For example, we may be able to see information about ourselves in the User table, but not other users.

Maybe a helper method for checking a search against security criteria, including whether or not to allow ad-hoc expressions, what tables & fields are allowed, which are not alloweed.

# Misc #