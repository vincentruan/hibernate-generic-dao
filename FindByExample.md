
---

Find by example functionality was added to the SearchFacade and DAOs in 0.4.3. Below is the thought process behind it. The conclusion is pretty much what was implemented.

---

## Feature Requirements ##
  * Should be simple: add as few additional classes and/or methods as reasonable.
  * Should be flexible...
    * Find by example should have all the same paging, sorting, fetching, and projection features as search.
    * Should have options, as hibernate criteria find by example, such as properties and values to exclude.
  * Must work easily with security checks.
  * Must work easily with property overrides that developers may include.

## Thoughts ##
To get all the flexible search options, I think we should have a way of converting an example to a search or adding an example to a search--maybe both.

If we convert the example into a filter, we can also use the same API to do security checks and property overrides that we use for search.

This conversion will need to have access to the SessionFactory in order to do introspection properly (get the hibernate property names right and do optimizations on id properties).

The conversion will also need to be publicly accessible so that developers have access to the Filter after it has been created. This will allow them to do the security checks, etc. before passing it to the search processor.

Currently the only public classes that have access to the session factory are DAOs themselves.




## Conclusion ##
Maybe we should add a method or several methods to the DAO that will convert an example object to a filter.

Add these methods to the base DAO, and add corresponding public methods to the original and standard DAOs.
```
protected Filter _filterFromExample(Object example);
protected Filter _filterFromExample(Object example, ExampleOptions options);
```

The `ExampleOptions` class would have these methods:
```
public ExampleOptions setExcludeProps(Collection<String>);
public Collection<String> getExcludeProps();
public ExampleOptions excludeProp(String); //add a property to the excludeProps collection
public ExampleOptions setExcludeNulls(boolean); //default TRUE
public boolean getExcludeNulls();
public ExampleOptions setExcludeZeros(boolean);
public boolean getExcludeZeros();
public ExampleOptions setIgnoreCase(boolean);
public boolean getIgnoreCase();
public ExampleOptions setLikeMode(int); //options (static constants on Example class): EXACT, START, END, ANYWHERE
public int getLikeMode();
```

Then if someone wants they can make a simple method on their own DAO like this...
```
public List<Project> findByExample(Project exampleProject) {
  Search s = new Search(Project.class);
  s.addFilter(filterFromExample(exampleProject));
  return search(s);
}
```

But they also have the freedom to add as many options as they want. For example...
```
public List<Project> findByExample(Project example, IMutableSearch additionalSearchOptions);

public int countByExample(Project example);

public int findByAnyExample(Project... examples);
```