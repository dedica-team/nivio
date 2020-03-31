import React, { useContext } from 'react';
import { useHistory } from 'react-router-dom';
import Terminal from 'react-console-emulator'
import CommandContext from '../../Context/Command.context';

const Command: React.FC = () => {

    const history = useHistory();
    const commandContext = useContext(CommandContext);

    const commands = () => {
        return {
            cd: {
                description: 'Back to the start.',
                usage: 'cd',
                fn: () => {
                    commandContext.message = "";
                    history.push("/");
                },
            },
            man: {
                description: 'Show the manual.',
                usage: 'man install|input|model|magic|extra|api',
                fn: (arg: string) => {
                    if(!arg){
                        arg = "install";
                    }
                    commandContext.message = 'RTFM: ' + arg;
                    history.push("/man/" + arg);
                }
            },
            sim: {
                description: 'Simulate realtime updates.',
                usage: 'sim',
                fn: () => {},
            }
        };
    };

    /*const sim = () => {
        if (landscape === null) {
            setMessage("Pick a landscape");
            return;
        }
        let circles = $('g.hexagon circle');
        let pick = circles[Math.floor(circles.length * Math.random())];
        setMessage(pick.id +' has a problem!');
        pick.style.setProperty('stroke', 'red');
    };*/

    return <footer key={'footer'} id={'footer'} style={{position: 'fixed', bottom: 0, width: '100%'}}>
    <div className={'typewriter'}>{commandContext.message}</div>
    <Terminal commands={commands()} promptLabel={'>'} autoFocus={true}
              style={{width: '100%', minHeight: null}} className={'console'}
              contentStyle={{padding: '0.5em'}}
              inputAreaStyle={{height: '1em'}}/>
    </footer>

};

export default Command;