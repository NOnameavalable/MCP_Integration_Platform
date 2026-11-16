1. **Signup**: As a new user, I want to sign up with a username and password, so that I have my own toolkit and keys.
2. **Login**: As a registered user, I want to log in, so that I can view and manage my own keys and toolkit.
3. **CreateApiKey**: As a logged-in user, I want to generate an API key, so that I can put it into an MCP client such as Claude Desktop to connect to the platform.
4. **RevokeApiKey**: As a logged-in user, I want to revoke one of my keys, so that it stops working immediately if it is leaked or no longer needed.
5. **SearchMarket**: As a logged-in user, I want to search the marketplace for MCP servers by keyword and see which tools each server provides, so that I can find the one I need.
6. **AddToToolkit**: As a logged-in user, I want to add a server from the marketplace to my toolkit, so that I can call its tools with my key afterwards.
7. **ListToolkit**: As a logged-in user, I want to view the servers and tools in my toolkit, so that I know what my key can currently call.
8. **RemoveFromToolkit**: As a logged-in user, I want to remove a server from my toolkit, so that my key can no longer call tools I don't need.
9. **Invoke**: As a user who has configured my key in Claude Desktop, I want my AI assistant to call the tools in my toolkit through the platform gateway and get the results back, so that I don't have to connect to each MCP server separately.