### JpaCursorItemReader
* ItemStream을 구현하고 있다.
*  API
```markdown
name
queryString(String jpql)
entityManagerFactory(EntityManagerFactory factory)
parameterValue(Map<String, Object> parameters)
maxItemCount(int count)
currentItemCount(int count) -> 조회 아이템의 시작 지점

```

