'''
Создает словарь по шаблону для дальнейшей обработи

'''
import database


class TemplateCreator:
    def __init__(self, name_template: str = None):
        self.template = (database
                         .JSONHandler("file.json")
                         .get_original_template())
        # self.res_dict = {name_template: []}
        self.temp_list = []

    def insert_field(self, tag_field: str, value_field: str):
        template_element = self.template["name_template"]
        for field in template_element:
            if field["tag_field"] == tag_field:
                field["value_field"] = value_field
                self.temp_list.append(field)
                return
        raise Exception("Error get_field_to_insert" + " " + tag_field)
    def create_field(self, name:str, tag:str, hint=None):
        element = {}
        element["name_field"] = name
        element["tag_field"] = tag
        element["value_field"] = ""
        element["type_field"] = "text"
        element["hint"] = hint
        self.temp_list.append(element)

    def get_result_list(self) -> list:
        return self.temp_list
