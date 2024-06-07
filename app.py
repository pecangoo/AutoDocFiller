from datetime import datetime

from flask import Flask, render_template, request, send_file, url_for, redirect

from AutoFiller import AutoFiller
import database
from TemplateCreator import TemplateCreator

app = Flask(__name__)


@app.route('/', methods=['GET'])
def ind():
    return redirect(url_for(f'index'))


@app.route('/test', methods=['GET'])
def test():
    if request.method == 'POST':
        for key in request.form.keys():
            print(key)

    data_template = (database
                     .JSONHandler("file.json")
                     .get_original_template())["name_template"]
    return render_template('main.html', data=data_template)


@app.route('/submit', methods=['POST'])
def submit():
    print(request.form.getlist('nameField[]'))
    print(request.form.getlist('tagField[]'))
    print(request.form.getlist('hintField[]'))
    templateCreator = TemplateCreator()
    for name, tag, hint in zip(request.form.getlist('nameField[]'),
                               request.form.getlist('tagField[]'),
                               request.form.getlist('hintField[]')):
        print(name + " " + tag + " " + hint)
        templateCreator.create_field(name=name,
                                     tag=tag,
                                     hint=hint)
    database.JSONHandler("file.json").update_dict_json("unnamed",
                                                       templateCreator.temp_list)

    # А print(data)  # Do something with the data
    return 'Data received successfully'


@app.route('/test2', methods=['GET', 'POST'])
def test2():
    # data_template = (database
    #                  .JSONHandler("file.json")
    #                  .get_original_template())["name_template"]
    return render_template('test2.html')


@app.route('/index/<name_template>',
           methods=['GET', 'POST'])
@app.route('/index',
           methods=['GET', 'POST'])
def index(name_template: str = None):
    # Этот момент для кнопки Конвертировать""
    if request.method == 'POST':
        print("19")
        file = request.files['file']
        auto_filler = AutoFiller(file)
        # if request.method == 'POST':
        for key in request.form.keys():
            if "date" in key:
                print("key: " + key)
                auto_filler.date_to_dict(tag=key, date_str=request.form[key])
            else:
                auto_filler.add_value_to_dict(key, request.form[key])
        auto_filler.replace_all_in_docs()
        newNameDoc = "Update_" + file.filename
        auto_filler.save_doc_to_file(newNameDoc)

        return send_file(newNameDoc, as_attachment=True)

    if name_template is None:
        # pass
        # # print("33")
        data_template = (database
                         .JSONHandler("file.json")
                         .get_original_template())["name_template"]
    else:
        print("36")
        data_template = (database
                         .JSONHandler("file.json")
                         .get_template_data(name_template))
        # data_template = data_template

    return render_template('index.html',
                           data=data_template)


@app.route('/accept', methods=['GET', 'POST'])
def accept():
    data_template = {}
    if request.method == 'POST':
        message = ""
        try:
            data = request.form.copy()
            template_creator = TemplateCreator()
            name = data["name_template"]
            del data["name_template"]
            for tag in data.keys():
                template_creator.insert_field(tag, data.get(tag))
            current_data = (database.JSONHandler("file.json")
                            .read_json_to_dict())
            print(template_creator.get_result_list())
            data_template = template_creator.get_result_list()
            current_data[name] = data_template

            (database.JSONHandler("file.json")
             .write_dict_to_json(current_data))
            print("OK")
            message = "Успешно сохраненно"
        except Exception as Ex:
            message = "Ошибка при сохранении"
            pass
    # message = "Шаблон успешно сохранен"
    # TODO: Добавить сообщение об успеху(chatGPT избранное)
    return render_template('index.html',
                           data=data_template,
                           message=message)
    # return redirect(url_for('index'))


@app.route('/choose_template')
def choose_template():
    current_data = database.JSONHandler("file.json").read_json_to_dict()
    names = []
    for element in current_data.keys():
        names.append(element)

    return render_template('templates.html',
                           names=names)


@app.route('/show_template/<name>', methods=['GET', 'POST'])
def show_template(name):
    if request.method == 'POST':
        print("Post")
    else:
        print("get")
    return redirect(url_for(f'index', name=name))
    # TODO: отсюда можем вызвать форму шаблона с заполнением полей
    # return f'Привет, {name}!'


@app.route('/', methods=['GET', 'POST'])
def main():
    return render_template('main.html')


if __name__ == '__main__':
    app.run()
