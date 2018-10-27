from domain import MyResources, Resource
from typing import List
from abc import ABC, abstractmethod


class Storage(ABC):
    @abstractmethod
    def load(self) -> MyResources:
        raise NotImplementedError

    @abstractmethod
    def save(self, my_resources):
        raise NotImplementedError


class Application:
    def __init__(self, storage: Storage):
        self.__storage: Storage = storage

    def list_my_resources(self) -> List[Resource]:
        return self.__storage.load().list()
