from domain import MyResources, Resource
from application import Storage, Application


class InMemoryStorage(Storage):
    def __init__(self):
        self.__my_resources = None

    def load(self) -> MyResources:
        return self.__my_resources

    def save(self, my_resources):
        self.__my_resources = my_resources


class TestApplication:
    def test_list_all_resources(self):
        my_resources_list = [
            Resource("A"),
            Resource("B")
        ]
        my_resources = MyResources(my_resources_list)
        storage = InMemoryStorage()
        self.given_my_resources_are(my_resources, storage)
        application = Application(storage)

        result = application.list_my_resources()

        assert result == my_resources_list

    @staticmethod
    def given_my_resources_are(my_resources: MyResources, storage: Storage):
        storage.save(my_resources)
