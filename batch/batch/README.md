
### Jdbc Cursor Item Reader

* Thread 안정성을 보장하지 않기 때문에 멀티 스레드 환경에서 사용할 경우 동시성 이슈가 발생하지 않도록 별도 동기화 처리가 필수적이다.


* JdbcCursorItemReaderBuilder API

```

name(String name)
fetchSize(int chunkSize) -> 데이터를 가지고 올 때 한번에 메모리에 할당할 크기(주로 청크 사이즈와 맞춘다)
datasource
rowMapeer -> 쿼리 결과와 객체 매핑을 위한 설정
beanRowMapper -> 별도의 RowMapper을 설정하지 않고 클래스 타입을 설정하면 자동으로 객체와 매핑
sql
queryArguments
maxItemCount -> 조회 최대 아이템 수
currentItemCount -> 조회 아이템 시작 시점
maxRows -> ResultSet 오브젝트가 포함할 수 있는 최대 행 수



```