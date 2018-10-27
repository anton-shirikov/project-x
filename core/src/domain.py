from typing import List


class Resource:
    def __init__(self, title: str):
        self.__title: str = title

    def __eq__(self, other) -> bool:
        return isinstance(other, Resource) and self.__title == other.__title


class MyResources:
    def __init__(self, resources: List[Resource]):
        self.__resources: List[Resource] = resources

    def list(self) -> List[Resource]:
        return self.__resources

    def add(self, resource: Resource) -> 'MyResources':
        updated_resources = self.__resources
        updated_resources.append(resource)
        return MyResources(updated_resources)
