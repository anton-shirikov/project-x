import * as React from 'react';
import axios from 'axios';

class Tag {
    public constructor(public readonly name: string,
                       public readonly value: string) {}
}

class TextEntry {
    public constructor(public readonly text: string,
                       public readonly tags: Tag[]) {}
}

interface State {
    entries: TextEntry[]
}

class App extends React.Component<{}, State> {

    public constructor(props: {}) {
        super(props);
        this.state = ({entries: []});
    }

    public componentDidMount() {
        axios.get("http://localhost:9090/resources")
            .then(resources => {
                this.setState({entries: resources.data.entries.map((r: any) => r.TextEntry)})
            })
            .catch(reason => {
                console.log(reason)
            });
    }

    public render() {
        return (
            <div>
                <h1>Entries</h1>
                <ul>
                    {this.state.entries.map(e => <li>{e.text}</li>)}
                </ul>
            </div>
        )
    }

}

export default App;
