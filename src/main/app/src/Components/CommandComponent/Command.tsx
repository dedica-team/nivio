import React, { useContext, useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import Terminal from 'react-console-emulator';
import CommandContext from '../../Context/Command.context';

import './Command.scss';

const Command: React.FC = () => {
  const history = useHistory();
  const commandContext = useContext(CommandContext);

  const [output, setOutput] = useState(commandContext.message);

  useEffect(() => {
    setOutput(commandContext.message);
  }, [commandContext.message]);

  const commands = () => {
    return {
      cd: {
        description: 'Back to the start.',
        usage: 'cd',
        fn: () => {
          commandContext.message = '';
          setOutput('');
          history.push('/');
        },
      },
      man: {
        description: 'Show the manual.',
        usage: 'man install|input|model|magic|extra|api',
        fn: (arg: string) => {
          if (!arg) {
            arg = 'install';
          }
          commandContext.message = 'RTFM: ' + arg;
          history.push('/man/' + arg);
        },
      },
      sim: {
        description: 'Simulate realtime updates.',
        usage: 'sim',
        fn: () => simulate(),
      },
    };
  };

  const simulate = () => {
    if (window.location.pathname.includes('/landscape/')) {
      const circles = document.getElementsByClassName('hexagon');
      if (circles.length > 0) {
        const randomCircle = circles[Math.floor(circles.length * Math.random())];
        const pick = randomCircle.children[0];
        setOutput(pick.id + ' has a problem!');
        (pick as HTMLElement).style.setProperty('stroke', 'red');
      } else {
        commandContext.message = 'Reload a landscape first';
        setOutput(commandContext.message);
      }
    } else {
      commandContext.message = 'Choose a landscape first';
      setOutput(commandContext.message);
    }
  };

  return (
    <footer key={'footer'} id={'footer'}>
      <div className={'typewriter'} data-testid='console'>
        {output}
      </div>
      <Terminal
        commands={commands()}
        promptLabel={'>'}
        autoFocus={true}
        errorText={'invalid command, type "help" for commands'}
        className={'console'}
        contentClassName={'consoleContent'}
      />
    </footer>
  );
};

export default Command;
