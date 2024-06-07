import json


class JSONHandler:
    def __init__(self, file_name):
        self.file_name = file_name

    def get_original_template(self) -> dict:
        return self.read_json_to_dict(file_name="original_template.json")

    def get_template_data(self, name_template) -> dict:
        return self.read_json_to_dict().get(name_template)

    def read_json_to_dict(self, file_name=None) -> dict:
        if file_name is None:
            f_name = self.file_name
        else:
            f_name = file_name
        with open(f_name, 'r') as file:
            data = json.load(file)
        return data

    def write_dict_to_json(self, data):
        with open(self.file_name, 'w',
                  encoding='utf-8') as file:
            json.dump(data, file, ensure_ascii=False)

    def update_dict_json(self,
                         name: str,
                         data: str):
        data_json = self.read_json_to_dict()
        data_json[name] = data
        self.write_dict_to_json(data_json)
