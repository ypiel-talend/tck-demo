# tck-demo
A connector to manage TODOs within this project: https://github.com/ypiel-talend/demo-todo

## How to build
```shell
mvn clean install
```

### Unit tests
Unit test need a TODO server launched on port `8989`.

### Launch the web tester
```shell
mvn talend-component:web -Dtalend.web.port=7171 -Dtalend.web.openBrowser=true
```


