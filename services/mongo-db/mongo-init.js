db.auth('admin', 'admin')

db = db.getSiblingDB('cats')

db.createUser({
  user: 'tester',
  pwd: 'tester',
  roles: [
    {
      role: 'root',
      db: 'admin',
    },
  ],
});

db.cats.createIndex({ identifier: 1 }, { unique: true });
db.cats.insert({ identifier: 1, name: 'Garfield', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-garfield-300x179.jpg' , movie: 'Garfield' });
db.cats.insert({ identifier: 2, name: 'Hello Kitty', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-hellokitty-300x261.jpg' , movie: '' });
db.cats.insert({ identifier: 3, name: 'Sylvester', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-sylvester-1-200x300.jpg' , movie: 'Looney Toons' });
db.cats.insert({ identifier: 4, name: 'Tigger', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-tigger-300x238.jpg' , movie: 'Winnie the Pooh' });
db.cats.insert({ identifier: 5, name: 'Simba', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-simba-300x211.jpg' , movie: 'The Lion King' });
db.cats.insert({ identifier: 6, name: 'Nala', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-nala-2.jpg' , movie: 'The Lion King' });
db.cats.insert({ identifier: 7, name: 'Nermal', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-nermal-300x286.jpg' , movie: 'Garfield' });
db.cats.insert({ identifier: 8, name: 'Figaro', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-figaro-300x233.jpg' , movie: 'Pinocchio' });
db.cats.insert({ identifier: 9, name: 'Grumpy Cat', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-grumpy-cat-300x158.jpg' , movie: '' });
db.cats.insert({ identifier: 10, name: 'Mr. Bigglesworth', imageUrl: 'http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-mrbigglesworth-300x198.jpg' , movie: 'Austin Powers' });
