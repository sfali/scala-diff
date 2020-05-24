class Foo {
    private var _name: String = _

    def name = {
        _name
    }

    def name_=(name: String): Unit = {
        _name = name
    }
}
