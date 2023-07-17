import sqlite3

# Connect to the database or create it if it doesn't exist
conn = sqlite3.connect('search_engine.db')

# Create a cursor object to execute SQL commands
cursor = conn.cursor()

# Create a table to store indexed data
cursor.execute('''
    CREATE TABLE IF NOT EXISTS indexed_data (
        id INTEGER PRIMARY KEY,
        url TEXT,
        title TEXT,
        content TEXT
    )
''')

# Create an index on the 'content' column for quick searching
cursor.execute('''
    CREATE INDEX IF NOT EXISTS idx_content ON indexed_data (content)
''')

# Function to insert data into the indexed_data table
def insert_data(url, title, content):
    cursor.execute('''
        INSERT INTO indexed_data (url, title, content)
        VALUES (?, ?, ?)
    ''', (url, title, content))
    conn.commit()

# Function to search for data in the indexed_data table
def search_data(keyword):
    cursor.execute('''
        SELECT url, title
        FROM indexed_data
        WHERE content LIKE ?
    ''', ('%' + keyword + '%',))

    results = cursor.fetchall()
    return results

# Insert some example data
insert_data("https://example.com/page1", "Example Page 1", "This is an example page with relevant content.")
insert_data("https://example.com/page2", "Example Page 2", "This is another example page with different content.")

# Search for data
search_results = search_data("example")

# Print the search results
for result in search_results:
    url, title = result
    print(f"URL: {url}\nTitle: {title}\n")

# Close the database connection
conn.close()
