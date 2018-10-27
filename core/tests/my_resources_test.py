from domain import MyResources, Resource


class TestMyResources:
    def test_i_can_view_a_list_of_my_resources(self):
        resource = Resource("something something something")
        my_resources = MyResources([resource])

        assert resource in my_resources.list()

    def test_i_can_add_a_new_resource(self):
        my_resources = MyResources([])

        my_resources.add(Resource("New"))

        assert Resource("New") in my_resources.list()
