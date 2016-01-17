from flask import Flask
from flask import render_template
app = Flask(__name__)

@app.route('/dashboard')
def dashboard():
	name= "CARLOS"
	return render_template('dashboard.html', name=name)

if __name__ == '__main__':
	app.run(host='localhost', port=5031)
	app.run(debug=True)
